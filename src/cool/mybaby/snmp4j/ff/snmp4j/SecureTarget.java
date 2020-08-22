/*_############################################################################
  _## 
  _##  SNMP4J - SecureTarget.java  
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

package cool.mybaby.snmp4j.ff.snmp4j;

import cool.mybaby.snmp4j.ff.snmp4j.security.SecurityLevel;
import cool.mybaby.snmp4j.ff.snmp4j.security.SecurityModel;
import cool.mybaby.snmp4j.ff.snmp4j.smi.Address;
import cool.mybaby.snmp4j.ff.snmp4j.smi.OctetString;

import java.io.Serializable;

/**
 * The <code>SecureTarget</code> is an security cool.mybaby.snmp4j.model independent abstract class
 * for all targets supporting secure SNMP communication.
 *
 * @author Jochen Katz & Frank Fock
 * @version 1.0
 */
public abstract class SecureTarget
        extends AbstractTarget implements Serializable {

    private static final long serialVersionUID = 3864834593299255038L;

    private int securityLevel = SecurityLevel.NOAUTH_NOPRIV;
    private int securityModel = SecurityModel.SECURITY_MODEL_ANY;
    private OctetString securityName = new OctetString();

    /**
     * Default constructor.
     */
    protected SecureTarget() {
    }

    /**
     * Creates a SNMPv3 secure target with an address and security name.
     *
     * @param address      an <code>Address</code> instance denoting the transport address of the
     *                     target.
     * @param securityName a <code>OctetString</code> instance representing the security name
     *                     of the USM user used to access the target.
     */
    protected SecureTarget(Address address, OctetString securityName) {
        super(address);
        setSecurityName(securityName);
    }

    /**
     * Gets the security cool.mybaby.snmp4j.model associated with this target.
     *
     * @return an <code>int</code> value as defined in the {@link SecurityModel}
     * interface or any third party subclass thereof.
     */
    public int getSecurityModel() {
        return securityModel;
    }

    /**
     * Gets the security name associated with this target. The security name
     * is used by the security cool.mybaby.snmp4j.model to lookup further parameters like
     * authentication and privacy protocol settings from the security cool.mybaby.snmp4j.model
     * dependent internal storage.
     *
     * @return an <code>OctetString</code> instance (never <code>null</code>).
     */
    public final OctetString getSecurityName() {
        return securityName;
    }

    /**
     * Gets the security level associated with this target.
     *
     * @return one of
     * <P><UL>
     * <LI>{@link SecurityLevel#NOAUTH_NOPRIV}
     * <LI>{@link SecurityLevel#AUTH_NOPRIV}
     * <LI>{@link SecurityLevel#AUTH_PRIV}
     * </UL></P>
     */
    public int getSecurityLevel() {
        return securityLevel;
    }

    /**
     * Sets the security level for this target. The supplied security level must
     * be supported by the security cool.mybaby.snmp4j.model dependent information associated with
     * the security name set for this target.
     *
     * @param securityLevel one of
     *                      <P><UL>
     *                      <LI>{@link SecurityLevel#NOAUTH_NOPRIV}
     *                      <LI>{@link SecurityLevel#AUTH_NOPRIV}
     *                      <LI>{@link SecurityLevel#AUTH_PRIV}
     *                      </UL></P>
     */
    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * Sets the security cool.mybaby.snmp4j.model for this target.
     *
     * @param securityModel an <code>int</code> value as defined in the {@link SecurityModel}
     *                      interface or any third party subclass thereof.
     */
    public void setSecurityModel(int securityModel) {
        this.securityModel = securityModel;
    }

    /**
     * Sets the security name to be used with this target.
     *
     * @param securityName an <code>OctetString</code> instance (must not be <code>null</code>).
     * @see #getSecurityName()
     */
    public final void setSecurityName(OctetString securityName) {
        this.securityName = securityName;
    }

}
