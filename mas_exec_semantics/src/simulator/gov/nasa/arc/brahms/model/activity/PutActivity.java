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

package gov.nasa.arc.brahms.model.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.Value;

/**
 * 
 * @author jhunter
 *put-activity 	::= put PAC.activity-name (
 *					{ PAC.param-decl [ , PAC.param-decl ]* } )
 *					{
 *					{ display : ID.literal-string ; }
 *					{ priority : [ ID.unsigned | PAC.param-name ] ; }
 *					{ random : [ ID.truth-value | PAC.param-name ] ; }
 *					{ min_duration : [ ID.unsigned | PAC.param-name ] ; }
 *					{ max_duration : [ ID.unsigned | PAC.param-name ] ; }
 *					{ PAC.resources }
 *					items
 *					{ destination : [OBJ.object-name | AGT.agent-name | ARE.area-name | PAC.param-name ] ; }
 *					{ when : [ start | end | PAC.param-name ] ; }
 *					}
 *
 *	items	 	::= items : [ PAC.param-name | OBJ.object-name | AGT.agent-name ]
 *					[ , [ PAC.param-name | OBJ.object-name | AGT.agent-name ]* ;
 */

public class PutActivity extends Activity {
	
	protected List<String> items;
	protected String destination;
	protected ParameterValue destVal = null;
	protected String when;
	protected ParameterValue whenVal = null;

	public PutActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		items = new ArrayList<String>();
		destination = "";
		when = "end";
	}
	
	public PutActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		items = new ArrayList<String>();
		destination = "";
		when = "end";
	}
	
	public void addItem(String item) {
		items.add(item);
	}
	
	public void setDestination(String dest) {
		destination = dest;
	}
	
	public void setDestination(Value dest) {
		if (dest instanceof StringValue)
			destination = ((StringValue) dest).getStringValue();
		else if (dest instanceof ParameterValue) {
			destination = null;
			destVal = (ParameterValue) dest;
		} else throw new RuntimeException("put dest must be string");
	}
	
	public void setWhen(String w) {
		this.when = w;
	}
	
	public void setWhen(Value w) {
		if (w instanceof StringValue)
			when = ((StringValue) w).getStringValue();
		else if (w instanceof ParameterValue) {
			when = null;
			whenVal = (ParameterValue) w;
		} else throw new RuntimeException("put when must be string");
	}
	
	public List<String> getItems() {
		return items;
	}
	
	
	private Parameter getParentParam(Basic b, String paramName) {
		if (b == null)
			return null;
		Stack<Object> stack = b.getCurrentWorkFrame();
		if (stack.size() > 1) {
			for (int i = stack.size() - 1; i >= 0; i--) {
				if (stack.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) stack.get(i);
					if (ai.getActivity() instanceof CompositeActivity) {
						CompositeActivity comp = (CompositeActivity) ai.getActivity();
						List<Parameter> parameters = comp.getParams();
						for (int j = 0; j < parameters.size(); j++) {
							if (parameters.get(j).getName().equals(paramName)) {
								return parameters.get(j);
							}
						}
					}
				}
			}
		} else throw new RuntimeException("***ERROR: couldn't find parameter for activity " + name);
		return null;
	}
	
	public String getDestination(Basic b) {
		if (destVal != null) {
			Parameter p = getParentParam(b, destVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: when parameter needs to be a string");
			}
			return s;
		}
		return destination;
	}
	
	public String getWhen(Basic b) {
		if (whenVal != null) {
			Parameter p = getParentParam(b, whenVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: when parameter needs to be a string");
			}
			return s;
		}
		return when;
	}
	
	public String toString(){	
		return "Put Acitivity:" +
						super.toString() +
						"-------\n";
	}
	
}
