/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR;

import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 * @author Gregory Jordan
 */
public class ObjectContainer {
    private final String name;
    private final List<CvrMesh> meshes;

    public ObjectContainer(String name, List<CvrMesh> meshes) {
        this.name = name;
        this.meshes = meshes;
    }
    
    
}
