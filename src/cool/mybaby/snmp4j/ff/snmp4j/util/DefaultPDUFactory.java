/*_############################################################################
  _## 
  _##  SNMP4J - DefaultPDUFactory.java  
  _## 
  _##  Copyright 2003-2007  Frank Fock and Jochen Katz (SNMP4J.org)
  _##  
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##  
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##  
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##  
  _##########################################################################*/


package cool.mybaby.snmp4j.ff.snmp4j.util;

import cool.mybaby.snmp4j.ff.snmp4j.PDU;
import cool.mybaby.snmp4j.ff.snmp4j.PDUv1;
import cool.mybaby.snmp4j.ff.snmp4j.ScopedPDU;
import cool.mybaby.snmp4j.ff.snmp4j.Target;
import cool.mybaby.snmp4j.ff.snmp4j.mp.SnmpConstants;

/**
 * The <code>DefaultPDUFactory</code> is a default implementation of the
 * <code>PDUFactory</code> interface. It creates PDUs depending on the
 * target's message processing cool.mybaby.snmp4j.model. That is, a {@link PDUv1} instance is
 * created for a SNMPv1 target whereas a {@link ScopedPDU} is created
 * for a SNMPv3 target. In all other cases a {@link PDU} instance is created.
 *
 * @author Frank Fock
 * @version 1.7.3
 * @since 1.0.4
 */
public class DefaultPDUFactory implements PDUFactory {

    private int pduType = PDU.GET;

    /**
     * Creates a PDU factory for the {@link PDU#GET} PDU type.
     */
    public DefaultPDUFactory() {
    }

    /**
     * Creates a PDU factory for the specified PDU type.
     *
     * @param pduType a PDU type as specified by {@link PDU}.
     */
    public DefaultPDUFactory(int pduType) {
        setPduType(pduType);
    }

    public void setPduType(int pduType) {
        this.pduType = pduType;
    }

    public int getPduType() {
        return pduType;
    }

    /**
     * Create a <code>PDU</code> instance for the supplied target.
     *
     * @param target the <code>Target</code> where the PDU to be created will be
     *               sent.
     * @return PDU a PDU instance that is compatible with the supplied target.
     */
    public PDU createPDU(Target target) {
        return createPDU(target, pduType);
    }

    /**
     * Create a <code>PDU</code> instance for the supplied target.
     *
     * @param target  the <code>Target</code> where the PDU to be created will be
     *                sent.
     * @param pduType a PDU type as specified by {@link PDU}.
     * @return PDU
     * a PDU instance that is compatible with the supplied target.
     */
    public static PDU createPDU(Target target, int pduType) {
        PDU request = createPDU(target.getVersion());
        request.setType(pduType);
        return request;
    }

    /**
     * Create a <code>PDU</code> instance for the specified SNMP version.
     *
     * @param targetVersion a SNMP version as defined by {@link SnmpConstants}.
     * @return a PDU instance that is compatible with the supplied target SNMP version.
     * @since 1.7.3
     */
    public static PDU createPDU(int targetVersion) {
        PDU request;
        switch (targetVersion) {
            case SnmpConstants.version3: {
                request = new ScopedPDU();
                break;
            }
            case SnmpConstants.version1: {
                request = new PDUv1();
                break;
            }
            default:
                request = new PDU();
        }
        return request;
    }
}
