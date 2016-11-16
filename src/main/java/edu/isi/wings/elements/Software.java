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

/**
 * Class for representing software metadata
 * @author dgarijo
 */
public class Software extends Resource{
    private String license;
    private String website;
    private String codeLocation;
    private String programmingLanguage;

    public Software() {
    }

    public Software(String name, String uri) {
        super(name, uri);
    }
    
    public String getCodeLocation() {
        return codeLocation;
    }

    public String getLicense() {
        return license;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public String getWebsite() {
        return website;
    }

    public void setCodeLocation(String codeLocation) {
        this.codeLocation = codeLocation;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    
}



