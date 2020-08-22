/*_############################################################################
  _## 
  _##  SNMP4J - ReadonlyVariableCallback.java  
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


package cool.mybaby.snmp4j.ff.snmp4j.smi;

/**
 * This abstract class helps to implement a {@link VariantVariableCallback}
 * for a read-only Variable.
 *
 * @author Frank Fock
 * @version 1.7
 * @since 1.7
 */
public abstract class ReadonlyVariableCallback implements VariantVariableCallback {

    public ReadonlyVariableCallback() {
    }

    public abstract void updateVariable(VariantVariable variable);

    public final void variableUpdated(VariantVariable variable) {
    }
}
