/**
Copyright © 2016, United States Government, as represented
by the Administrator of the National Aeronautics and Space
Administration. All rights reserved.
 
The MAV - Modeling, analysis and visualization of ATM concepts
platform is licensed under the Apache License, Version 2.0
(the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific
language governing permissions and limitations under the
License.
**/

package gov.nasa.arc.brahms.vm.api;

import gov.nasa.arc.brahms.vm.api.common.IModel;
import gov.nasa.arc.brahms.vm.api.components.IVMController;
import gov.nasa.arc.brahms.vm.api.components.IWorldState;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.japi.common.JModel;
import gov.nasa.arc.brahms.vm.japi.common.JWorldState;

public class JAPI {

	private static JWorldState worldState = new JWorldState();
	
	private static JModel jModel;
	
	 public final static IWorldState getWorldState() throws ExternalException {
			return worldState;	
	 } //
	 
	 public final static IModel getModel() {
		 return jModel;
	 }
	 
	 public synchronized final static IVMController getVMController()
			 				throws ExternalException {
		throw new RuntimeException("need to implement");
	 } // getVMController


}
