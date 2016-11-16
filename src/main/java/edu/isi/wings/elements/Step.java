/*
 *  Copyright 2016 Daniel Garijo Verdejo, Information Sciences Institute, USC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package edu.isi.wings.elements;

import java.util.ArrayList;

/**
 * @author dgarijo
 */
public class Step extends Resource{
    //sometimes it may be useful to have  the step names, code and uri on a single class.
    //motif as well
    // this way it is easier to initialize them from the KB
    String codeLocation;
    Step implementationOf;//only for execution processes, this is a shortcut to link the abstract step 
    ArrayList<String> motifs;//motifs owned by the Step

    public Step() {
    }
    
    public Step(String name, String uri) {
        super(name, uri);
    }

    public String getCodeLocation() {
        return codeLocation;
    }

    public void setCodeLocation(String codeLocation) {
        this.codeLocation = codeLocation;
    }

    public ArrayList<String> getMotifs() {
        return motifs;
    }

    public void setMotifs(ArrayList<String> motifs) {
        this.motifs = motifs;
    }

    public Step getImplementationOf() {
        return implementationOf;
    }

    public void setImplementationOf(Step implementationOf) {
        this.implementationOf = implementationOf;
    }
    
    
    
    
    
}
