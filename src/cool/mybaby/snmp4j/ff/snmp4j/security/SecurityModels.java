/*_############################################################################
  _## 
  _##  SNMP4J - SecurityModels.java  
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


package cool.mybaby.snmp4j.ff.snmp4j.security;

import cool.mybaby.snmp4j.ff.snmp4j.smi.Integer32;

import java.util.Hashtable;

/**
 * The <code>SecurityModels</code> class is a collection of all
 * supported security models of a SNMP entity.
 *
 * @author Jochen Katz & Frank Fock
 * @version 1.0a
 */
public class SecurityModels {

    private Hashtable securityModels = new Hashtable(3);

    private static SecurityModels instance = null;

    protected SecurityModels() {
    }

    /**
     * Gets the security singleton instance.
     *
     * @return the <code>SecurityModels</code> instance.
     */
    public synchronized static SecurityModels getInstance() {
        if (instance == null) {
            instance = new SecurityModels();
        }
        return instance;
    }

    /**
     * Adds a security cool.mybaby.snmp4j.model to the central repository of security models.
     *
     * @param model a <code>SecurityModel</code>. If a security cool.mybaby.snmp4j.model with the same ID
     *              already
     */
    public void addSecurityModel(SecurityModel model) {
        securityModels.put(new Integer32(model.getID()), model);
    }

    /**
     * Removes a security cool.mybaby.snmp4j.model from the central repository of security models.
     *
     * @param id the <code>Integer32</code> ID of the security cool.mybaby.snmp4j.model to remove.
     * @return the removed <code>SecurityModel</code> or <code>null</code> if
     * <code>id</code> is not registered.
     */
    public SecurityModel removeSecurityModel(Integer32 id) {
        return (SecurityModel) securityModels.remove(id);
    }

    /**
     * Returns a security cool.mybaby.snmp4j.model from the central repository of security models.
     *
     * @param id the <code>Integer32</code> ID of the security cool.mybaby.snmp4j.model to return.
     * @return the with <code>id</code> associated <code>SecurityModel</code> or
     * <code>null</code> if no such cool.mybaby.snmp4j.model is registered.
     */
    public SecurityModel getSecurityModel(Integer32 id) {
        return (SecurityModel) securityModels.get(id);
    }
}

