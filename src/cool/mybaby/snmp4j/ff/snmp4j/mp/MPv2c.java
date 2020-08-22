/*_############################################################################
  _## 
  _##  SNMP4J - MPv2c.java  
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


package cool.mybaby.snmp4j.ff.snmp4j.mp;

import cool.mybaby.snmp4j.ff.snmp4j.MessageDispatcher;
import cool.mybaby.snmp4j.ff.snmp4j.MutablePDU;
import cool.mybaby.snmp4j.ff.snmp4j.PDU;
import cool.mybaby.snmp4j.ff.snmp4j.ScopedPDU;
import cool.mybaby.snmp4j.ff.snmp4j.asn1.BER;
import cool.mybaby.snmp4j.ff.snmp4j.asn1.BER.MutableByte;
import cool.mybaby.snmp4j.ff.snmp4j.asn1.BERInputStream;
import cool.mybaby.snmp4j.ff.snmp4j.asn1.BEROutputStream;
import cool.mybaby.snmp4j.ff.snmp4j.log.LogAdapter;
import cool.mybaby.snmp4j.ff.snmp4j.log.LogFactory;
import cool.mybaby.snmp4j.ff.snmp4j.security.SecurityLevel;
import cool.mybaby.snmp4j.ff.snmp4j.security.SecurityModel;
import cool.mybaby.snmp4j.ff.snmp4j.security.SecurityModels;
import cool.mybaby.snmp4j.ff.snmp4j.smi.Address;
import cool.mybaby.snmp4j.ff.snmp4j.smi.Integer32;
import cool.mybaby.snmp4j.ff.snmp4j.smi.OctetString;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The <code>MPv2c</code> is the message processing cool.mybaby.snmp4j.model for SNMPv2c
 * (community based SNMPv2).
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MPv2c implements MessageProcessingModel {

    public static final int ID = MessageProcessingModel.MPv2c;
    private static final LogAdapter logger = LogFactory.getLogger(MPv2c.class);

    public MPv2c() {
    }

    public int getID() {
        return ID;
    }

    public int prepareOutgoingMessage(Address transportAddress,
                                      int maxMessageSize,
                                      int messageProcessingModel,
                                      int securityModel,
                                      byte[] securityName,
                                      int securityLevel,
                                      PDU pdu,
                                      boolean expectResponse,
                                      PduHandle sendPduHandle,
                                      Address destTransportAddress,
                                      BEROutputStream outgoingMessage)
            throws IOException {
        if ((securityLevel != SecurityLevel.NOAUTH_NOPRIV) ||
                (securityModel != SecurityModel.SECURITY_MODEL_SNMPv2c)) {
            logger.error("MPv2c used with unsupported security cool.mybaby.snmp4j.model");
            return SnmpConstants.SNMP_MP_UNSUPPORTED_SECURITY_MODEL;
        }
        if (pdu instanceof ScopedPDU) {
            String txt = "ScopedPDU must not be used with MPv2c";
            logger.error(txt);
            throw new IllegalArgumentException(txt);
        }

        if (!isProtocolVersionSupported(messageProcessingModel)) {
            logger.error("MPv2c used with unsupported SNMP version");
            return SnmpConstants.SNMP_MP_UNSUPPORTED_SECURITY_MODEL;
        }

        OctetString community = new OctetString(securityName);
        Integer32 version = new Integer32(messageProcessingModel);
        // compute total length
        int length = pdu.getBERLength();
        length += community.getBERLength();
        length += version.getBERLength();

        ByteBuffer buf = ByteBuffer.allocate(length +
                BER.getBERLengthOfLength(length) + 1);
        // set the buffer of the outgoing message
        outgoingMessage.setBuffer(buf);

        // encode the message
        BER.encodeHeader(outgoingMessage, BER.SEQUENCE, length);
        version.encodeBER(outgoingMessage);

        community.encodeBER(outgoingMessage);
        pdu.encodeBER(outgoingMessage);

        return SnmpConstants.SNMP_MP_OK;
    }

    public int prepareResponseMessage(int messageProcessingModel,
                                      int maxMessageSize,
                                      int securityModel,
                                      byte[] securityName,
                                      int securityLevel,
                                      PDU pdu,
                                      int maxSizeResponseScopedPDU,
                                      StateReference stateReference,
                                      StatusInformation statusInformation,
                                      BEROutputStream outgoingMessage)
            throws IOException {
        return prepareOutgoingMessage(stateReference.getAddress(),
                maxMessageSize,
                messageProcessingModel,
                securityModel,
                securityName,
                securityLevel,
                pdu,
                false,
                stateReference.getPduHandle(),
                null,
                outgoingMessage);
    }

    public int prepareDataElements(MessageDispatcher messageDispatcher,
                                   Address transportAddress,
                                   BERInputStream wholeMsg,
                                   Integer32 messageProcessingModel,
                                   Integer32 securityModel,
                                   OctetString securityName,
                                   Integer32 securityLevel,
                                   MutablePDU pdu,
                                   PduHandle sendPduHandle,
                                   Integer32 maxSizeResponseScopedPDU,
                                   StatusInformation statusInformation,
                                   MutableStateReference stateReference)
            throws IOException {

        MutableByte mutableByte = new MutableByte();
        int length = BER.decodeHeader(wholeMsg, mutableByte);
        int startPos = (int) wholeMsg.getPosition();

        if (mutableByte.getValue() != BER.SEQUENCE) {
            String txt = "SNMPv2c PDU must start with a SEQUENCE";
            logger.error(txt);
            throw new IOException(txt);
        }
        Integer32 version = new Integer32();
        version.decodeBER(wholeMsg);

        securityName.decodeBER(wholeMsg);
        securityLevel.setValue(SecurityLevel.NOAUTH_NOPRIV);
        securityModel.setValue(SecurityModel.SECURITY_MODEL_SNMPv2c);
        messageProcessingModel.setValue(ID);

        PDU v2cPDU = new PDU();
        pdu.setPdu(v2cPDU);
        v2cPDU.decodeBER(wholeMsg);

        BER.checkSequenceLength(length,
                (int) wholeMsg.getPosition() - startPos,
                v2cPDU);

        sendPduHandle.setTransactionID(v2cPDU.getRequestID().getValue());

        // create state reference
        StateReference stateRef =
                new StateReference(sendPduHandle,
                        transportAddress,
                        null,
                        SecurityModels.getInstance().getSecurityModel(securityModel),
                        securityName.getValue(),
                        SnmpConstants.SNMP_ERROR_SUCCESS);
        stateReference.setStateReference(stateRef);

        return SnmpConstants.SNMP_MP_OK;
    }

    public boolean isProtocolVersionSupported(int snmpProtocolVersion) {
        return (snmpProtocolVersion == SnmpConstants.version2c);
    }

    public void releaseStateReference(PduHandle pduHandle) {
        // we do not cache state information -> do nothing
    }
}