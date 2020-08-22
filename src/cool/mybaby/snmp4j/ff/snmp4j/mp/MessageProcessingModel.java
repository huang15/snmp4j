/*_############################################################################
  _## 
  _##  SNMP4J - MessageProcessingModel.java  
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

import cool.mybaby.snmp4j.ff.snmp4j.*;
import cool.mybaby.snmp4j.ff.snmp4j.asn1.BERInputStream;
import cool.mybaby.snmp4j.ff.snmp4j.asn1.BEROutputStream;
import cool.mybaby.snmp4j.ff.snmp4j.security.SecurityLevel;
import cool.mybaby.snmp4j.ff.snmp4j.security.SecurityModel;
import cool.mybaby.snmp4j.ff.snmp4j.smi.Address;
import cool.mybaby.snmp4j.ff.snmp4j.smi.Integer32;
import cool.mybaby.snmp4j.ff.snmp4j.smi.OctetString;

import java.io.IOException;

// needed by JavaDoc:

/**
 * The <code>MessageProcessingModel</code> interface defines common methods
 * to all SNMP message processing models.
 * <p>Note: The read counter of the {@link BERInputStream} parameters in this
 * interface should not be reset while those methods are executed.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface MessageProcessingModel {

    int MPv1 = 0;
    int MPv2c = 1;
    int MPv2u = 2;
    int MPv3 = 3;

    /**
     * Gets the numerical ID of the message processing cool.mybaby.snmp4j.model as defined by the
     * constants in this interface or by an appropriate constant in the
     * class implementing this interface.
     *
     * @return a positive integer value.
     */
    int getID();

    /**
     * Prepares an outgoing message as defined in RFC3412 �7.1.
     *
     * @param transportAddress       the destination transport <code>Address</code>.
     * @param maxMsgSize             the maximum message size the transport mapping for the destination
     *                               address is capable of.
     * @param messageProcessingModel the {@link MessageProcessingModel} ID (typically, the SNMP version).
     * @param securityModel          the security cool.mybaby.snmp4j.model ID (see {@link SecurityModel}) to use.
     * @param securityName           the principal on behalf the message is to be sent.
     * @param securityLevel          the level of security requested (see {@link SecurityLevel}).
     * @param pdu                    the <code>PDU</code> to send. For a SNMPv1 trap <code>pdu</code> has
     *                               to be a {@link PDUv1} instance, for SNMPv3 messages it has to be a
     *                               {@link ScopedPDU} instance.
     * @param expectResponse         indicates if a message expects a response. This has to be
     *                               <code>true</code> for confirmed class PDUs and <code>false</code>
     *                               otherwise.
     * @param sendPduHandle          the <code>PduHandle</code> that uniquely identifies the sent message.
     * @param destTransportAddress   returns the destination transport address (currently set always set to
     *                               <code>transportAddress</code>.
     * @param outgoingMessage        returns the message to send.
     * @return the status of the message preparation. {@link SnmpConstants#SNMP_MP_OK}
     * is returned if on success, otherwise any of the
     * <code>SnmpConstants.SNMP_MP_*</code> values may be returned.
     * @throws IOException if the supplied PDU could not be encoded to the
     *                     <code>outgoingMessage</code>
     */
    int prepareOutgoingMessage(Address transportAddress,
                               int maxMsgSize,
                               int messageProcessingModel,
                               int securityModel,
                               byte[] securityName,
                               int securityLevel,
                             /* the following parameters are given in ScopedPDU
                                   byte[] contextEngineID,
                                   byte[] contextName,
                              */
                               PDU pdu,
                               boolean expectResponse,
                               PduHandle sendPduHandle,
                               Address destTransportAddress,
                               BEROutputStream outgoingMessage)
            throws IOException;

    /**
     * Prepares a response message as defined in RFC3412 �7.1.
     *
     * @param messageProcessingModel   int
     *                                 the {@link MessageProcessingModel} ID (typically, the SNMP version).
     * @param maxMsgSize               the maximum message size the transport mapping for the destination
     *                                 address is capable of.
     * @param securityModel            the security cool.mybaby.snmp4j.model ID (see {@link SecurityModel}) to use.
     * @param securityName             the principal on behalf the message is to be sent.
     * @param securityLevel            the level of security requested (see {@link SecurityLevel}).
     * @param pdu                      the <code>PDU</code> to send. For a SNMPv1 trap <code>pdu</code> has
     *                                 to be a {@link PDUv1} instance, for SNMPv3 messages it has to be a
     *                                 {@link ScopedPDU} instance.
     * @param maxSizeResponseScopedPDU the maximum size of the scoped PDU the sender (of the request) can
     *                                 accept.
     * @param stateReference           reference to state information presented with the request.
     * @param statusInformation        returns success or error indication. When an error occured, the error
     *                                 counter OID and value are included.
     * @param outgoingMessage          returns the message to send.
     * @return the status of the message preparation. {@link SnmpConstants#SNMP_MP_OK}
     * is returned if on success, otherwise any of the
     * <code>SnmpConstants.SNMP_MP_*</code> values may be returned.
     * @throws IOException if an internal error or a resource exception occured.
     */
    int prepareResponseMessage(int messageProcessingModel,
                               int maxMsgSize,
                               int securityModel,
                               byte[] securityName,
                               int securityLevel,
                             /* the following parameters are given in ScopedPDU
                                   byte[] contextEngineID,
                                   byte[] contextName,
                              */
                               PDU pdu,
                               int maxSizeResponseScopedPDU,
                               StateReference stateReference,
                               StatusInformation statusInformation,
                               BEROutputStream outgoingMessage)
            throws IOException;

    /**
     * Prepare data elements from an incoming SNMP message as described in
     * RFC3412 �7.2.
     *
     * @param messageDispatcher        the <code>MessageDispatcher</code> instance to be used to send reports.
     *                                 Thus, <code>messageDispatcher</code> is typically the calling module.
     * @param transportAddress         the origin transport address.
     * @param wholeMsg                 the whole message as received from the network.
     * @param messageProcessingModel   returns the message processing cool.mybaby.snmp4j.model (typically the SNMP version).
     * @param securityModel            returns the security cool.mybaby.snmp4j.model ID (see {@link SecurityModel}.
     * @param securityName             returns the principal.
     * @param securityLevel            returns the requested security level (see {@link SecurityLevel}).
     * @param pdu                      returns SNMP protocol data unit (the payload of the received message).
     * @param sendPduHandle            returns the handle to match request.
     * @param maxSizeResponseScopedPDU returns the maximum size of the scoped PDU the sender can accept.
     * @param statusInformation        returns success or error indication. When an error occured, the error
     *                                 counter OID and value are included.
     * @param stateReference           returns the state reference to be used for a possible response. On input
     *                                 the stateReference may contain information about the transport mapping
     *                                 of the incoming request. This allows the
     *                                 <code>MessageProcessingModel</code> to send reports over the same
     *                                 transport as it received them.
     * @return int
     * the status of the message preparation. {@link SnmpConstants#SNMP_MP_OK}
     * is returned on success, otherwise any of the
     * <code>SnmpConstants.SNMP_MP_*</code> values may be returned.
     * @throws IOException if the decoding of the message failed.
     */
    int prepareDataElements(MessageDispatcher messageDispatcher,
                            Address transportAddress,
                            BERInputStream wholeMsg,
                            Integer32 messageProcessingModel,
                            Integer32 securityModel,
                            OctetString securityName,
                            Integer32 securityLevel,
                          /* the following parameters are given in ScopedPDU
                                byte[] contextEngineID,
                                byte[] contextName,
                           */
                            MutablePDU pdu,
                            PduHandle sendPduHandle,
                            Integer32 maxSizeResponseScopedPDU,
                            StatusInformation statusInformation,
                            MutableStateReference stateReference)
            throws IOException;

    /**
     * Checks whether the supplied SNMP protocol version is supported by this
     * message processing cool.mybaby.snmp4j.model.
     *
     * @param snmpProtocolVersion the SNMP protocol version.
     * @return <code>true</code> if the supplied SNMP protocol is supported,
     * <code>false</code> otherwise.
     */
    boolean isProtocolVersionSupported(int snmpProtocolVersion);

    /**
     * Release the state reference associated with the supplied
     * <code>PduHandle</code>.
     *
     * @param pduHandle a <code>PduHandle</code>.
     */
    void releaseStateReference(PduHandle pduHandle);
}

