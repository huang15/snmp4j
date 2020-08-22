package cool.mybaby.snmp4j;

import cool.mybaby.snmp4j.MIB.printer;
import cool.mybaby.snmp4j.component.Supplies;
import cool.mybaby.snmp4j.ff.snmp4j.CommunityTarget;
import cool.mybaby.snmp4j.ff.snmp4j.PDU;
import cool.mybaby.snmp4j.ff.snmp4j.Snmp;
import cool.mybaby.snmp4j.ff.snmp4j.TransportMapping;
import cool.mybaby.snmp4j.ff.snmp4j.event.ResponseEvent;
import cool.mybaby.snmp4j.ff.snmp4j.mp.SnmpConstants;
import cool.mybaby.snmp4j.ff.snmp4j.smi.*;
import cool.mybaby.snmp4j.ff.snmp4j.transport.DefaultUdpTransportMapping;
import cool.mybaby.snmp4j.ff.snmp4j.util.DefaultPDUFactory;
import cool.mybaby.snmp4j.ff.snmp4j.util.TreeEvent;
import cool.mybaby.snmp4j.ff.snmp4j.util.TreeUtils;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@SuppressWarnings({"unused", "rawtypes"})
public class snmp4j {
    public static final String Pub = "public";
    public static final String localhost = "localhost";
    public static final int defaultPort = 161;
    public static final int defaultRetries = 1;
    public static final int defaultTimeout = 3000;

    protected String host;
    protected int port = snmp4j.defaultPort;
    protected int retries = 1;
    protected int timeout = 3000;

    public snmp4j(String host) {
        this.host = host;
    }

    public snmp4j(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected static boolean isNullOrEmpty(String expr) {
        return expr == null || expr.equals("");
    }

    protected static boolean notNullOrEmpty(String expr) {
        return !snmp4j.isNullOrEmpty(expr);
    }

    protected static boolean isNullOrZeroLength(Object expr) {
        return expr == null || !expr.getClass().isArray() || Array.getLength(expr) == 0;
    }

    protected static boolean notNullOrZeroLength(Object expr) {
        return !snmp4j.isNullOrZeroLength(expr);
    }

    protected static int parseInt(String expr) {
        int value = 0;
        if (snmp4j.notNullOrEmpty(expr)) {
            try {
                value = Integer.parseInt(expr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    protected static CommunityTarget newCommunityTarget(String host) {
        return snmp4j.newCommunityTarget(host, snmp4j.defaultPort, snmp4j.defaultRetries, snmp4j.defaultTimeout);
    }

    protected static CommunityTarget newCommunityTarget(String host, int port) {
        return snmp4j.newCommunityTarget(host, port, snmp4j.defaultRetries, snmp4j.defaultTimeout);
    }

    protected static CommunityTarget newCommunityTarget(String host, int port, int retries, int timeout) {
        CommunityTarget community = null;
        if (snmp4j.notNullOrEmpty(host)) {
            Address addr = GenericAddress.parse("udp:" + host + "/" + port);
            if (addr != null) {
                community = new CommunityTarget();
                community.setCommunity(new OctetString(snmp4j.Pub));
                community.setAddress(addr);
                community.setRetries(retries);
                community.setTimeout(timeout);
                community.setVersion(SnmpConstants.version2c);
            }
        }
        return community;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public String invoke(String oid, boolean isGetNext) {
        String value = null;
        try {
            if (snmp4j.notNullOrEmpty(oid) &&
                    snmp4j.notNullOrEmpty(this.host) &&
                    this.port >= 0 &&
                    this.port <= 65535) {
                CommunityTarget communityTarget = snmp4j.newCommunityTarget(this.host, this.port, this.retries,
                        this.timeout);
                if (communityTarget != null) {
                    TransportMapping transport = new DefaultUdpTransportMapping();
                    Snmp snmp = new Snmp(transport);
                    transport.listen();
                    PDU pdu = new PDU();
                    pdu.add(new VariableBinding(new OID(oid)));
                    for (int i = 1; i <= (isGetNext ? 2 : 1); i++) {
                        ResponseEvent e;
                        if (i == 1) {
                            e = snmp.get(pdu, communityTarget);
                        } else {
                            e = snmp.getNext(pdu, communityTarget);
                        }
                        if (e != null && e.getResponse() != null) {
                            Vector recVBs = e.getResponse().getVariableBindings();
                            for (int j = 0; j < recVBs.size(); j++) {
                                VariableBinding recVB = (VariableBinding) recVBs.elementAt(j);
                                if (!recVB.isException()) {
                                    Variable var = recVB.getVariable();
                                    value = recVB.getVariable().toString();
                                    if (Null.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (Counter64.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (Integer32.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (OID.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (UnsignedInteger32.class.isAssignableFrom(var.getClass())) {

                                    } else if (SMIAddress.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (VariantVariable.class.isAssignableFrom(var.getClass())) {
                                        assert (false);
                                    } else if (OctetString.class.isAssignableFrom(var.getClass())) {

                                    } else {
                                        assert (false);
                                    }
                                    break;
                                }
                            }
                        }
                        if (value != null) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return value;
    }

    @SuppressWarnings("rawtypes")
    public static LinkedHashMap<String, String> walk(String host, String tableOid) {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();
            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List events = treeUtils.getSubtree(snmp4j.newCommunityTarget(host), new OID(tableOid));
            if (events != null) {
                for (Object e : events) {
                    if (e instanceof TreeEvent) {
                        TreeEvent event = (TreeEvent) e;
                        if (!event.isError()) {
                            VariableBinding[] varBindings = event.getVariableBindings();
                            if (snmp4j.notNullOrZeroLength(varBindings)) {
                                for (VariableBinding varBinding : varBindings) {
                                    if (varBinding != null) {
                                        result.put("." + varBinding.getOid().toString(),
                                                varBinding.getVariable().toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("DuplicatedCode")
    public Supplies supplies() {
        Supplies supplies = null;
        if (snmp4j.notNullOrEmpty(this.host)) {
            supplies = new Supplies();
            for (Map.Entry<String, String> e : snmp4j.walk(this.host, printer.prtMarkerSuppliesLevel).entrySet()) {
                if (snmp4j.notNullOrEmpty(e.getKey())) {
                    switch (snmp4j.parseInt(e.getKey().substring(e.getKey().lastIndexOf('.') + 1))) {
                        case 1:
                            supplies.Key = snmp4j.parseInt(e.getValue());
                            break;
                        case 30:
                            supplies.Key1 = snmp4j.parseInt(e.getValue());
                            break;
                        case 31:
                            supplies.Key2 = snmp4j.parseInt(e.getValue());
                            break;
                        case 2:
                            supplies.Yellow = snmp4j.parseInt(e.getValue());
                            break;
                        case 3:
                            supplies.Magenta = snmp4j.parseInt(e.getValue());
                            break;
                        case 4:
                            supplies.Cyan = snmp4j.parseInt(e.getValue());
                            break;
                        case 6:
                            supplies.DurmKey = snmp4j.parseInt(e.getValue());
                            break;
                        case 7:
                            supplies.DurmYellow = snmp4j.parseInt(e.getValue());
                            break;
                        case 8:
                            supplies.DurmMagenta = snmp4j.parseInt(e.getValue());
                            break;
                        case 9:
                            supplies.DurmCyan = snmp4j.parseInt(e.getValue());
                            break;
                    }
                }
            }
            for (Map.Entry<String, String> e : snmp4j.walk(this.host, printer.prtMarkerSuppliesMaxCapacity).entrySet()) {
                if (snmp4j.notNullOrEmpty(e.getKey())) {
                    switch (snmp4j.parseInt(e.getKey().substring(e.getKey().lastIndexOf('.') + 1))) {
                        case 1:
                            supplies.MaxKey = snmp4j.parseInt(e.getValue());
                            break;
                        case 30:
                            supplies.MaxKey1 = snmp4j.parseInt(e.getValue());
                            break;
                        case 31:
                            supplies.MaxKey2 = snmp4j.parseInt(e.getValue());
                            break;
                        case 2:
                            supplies.MaxYellow = snmp4j.parseInt(e.getValue());
                            break;
                        case 3:
                            supplies.MaxMagenta = snmp4j.parseInt(e.getValue());
                            break;
                        case 4:
                            supplies.MaxCyan = snmp4j.parseInt(e.getValue());
                            break;
                        case 6:
                            supplies.MaxDurmKey = snmp4j.parseInt(e.getValue());
                            break;
                        case 7:
                            supplies.MaxDurmYellow = snmp4j.parseInt(e.getValue());
                            break;
                        case 8:
                            supplies.MaxDurmMagenta = snmp4j.parseInt(e.getValue());
                            break;
                        case 9:
                            supplies.MaxDurmCyan = snmp4j.parseInt(e.getValue());
                            break;
                    }
                }
            }
            for (Map.Entry<String, String> e : snmp4j.walk(this.host, printer.prtMarkerSuppliesSupplyUnit).entrySet()) {
                if (snmp4j.notNullOrEmpty(e.getKey())) {
                    switch (snmp4j.parseInt(e.getKey().substring(e.getKey().lastIndexOf('.') + 1))) {
                        case 1:
                            supplies.KeyUnit = snmp4j.parseInt(e.getValue());
                            break;
                        case 30:
                            supplies.KeyUnit1 = snmp4j.parseInt(e.getValue());
                            break;
                        case 31:
                            supplies.KeyUnit2 = snmp4j.parseInt(e.getValue());
                            break;
                        case 2:
                            supplies.YellowUnit = snmp4j.parseInt(e.getValue());
                            break;
                        case 3:
                            supplies.MagentaUnit = snmp4j.parseInt(e.getValue());
                            break;
                        case 4:
                            supplies.CyanUnit = snmp4j.parseInt(e.getValue());
                            break;
                        case 6:
                            supplies.DurmKeyUnit = snmp4j.parseInt(e.getValue());
                            break;
                        case 7:
                            supplies.DurmYellowUnit = snmp4j.parseInt(e.getValue());
                            break;
                        case 8:
                            supplies.DurmMagentaUnit = snmp4j.parseInt(e.getValue());
                            break;
                        case 9:
                            supplies.DurmCyanUnit = snmp4j.parseInt(e.getValue());
                            break;
                    }
                }
            }
        }
        return supplies;
    }

    public static String Get(String oid) {
        return new snmp4j(snmp4j.localhost).invoke(oid, false);
    }

    public static String GetNext(String oid) {
        return new snmp4j(snmp4j.localhost).invoke(oid, true);
    }
}
