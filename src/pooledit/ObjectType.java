/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pooledit;

/**
 *
 * @author alan
 */
public class ObjectType {
    public String type;
    public String description;
    
    public ObjectType(String name, String desc) {
        this.type = name;
        this.description = desc;
    }
}
