/*
 * Copyright (C) 2016 Iceberg7
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jac.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Iceberg7
 */



 
/** Sample 2 - How to use nodes as handles to manipulate objects in the scene.
 * You can rotate, translate, and scale objects by manipulating their parent nodes.
 * The Root Node is special: Only what is attached to the Root Node appears in the scene. */
public class testjmonkey extends SimpleApplication {
 
     public static void main(String[] args){
        testjmonkey app = new testjmonkey();
        app.start();
    }
 
    
    Node pivot;
    
    
    void read(Node core) throws IOException {
        
       
       
        Scanner scanner = new Scanner(new FileInputStream("C:\\Users\\Iceberg7\\Documents\\Programing Projects\\reverse it\\test.txt"), "UTF-8");
        try {
            String tmp;
            String[] strarray;
            
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
         
            
         
            
            tmp = scanner.nextLine();
            int tmplength = Integer.parseInt(tmp);
            System.out.println(tmplength);
            scanner.nextLine();
            Vector3f[] lineVerticies = new Vector3f[tmplength];
            float[] normals = new float[lineVerticies.length*3];
            float[] colors = new float[lineVerticies.length*4];
            for(int i = 0; i < normals.length; i=i+3){
                normals[i] = 0;
                normals[i+1] = 0;
                normals[i+2] = 1;
            }
           
            int linecount = 0;
            while (linecount < lineVerticies.length-1) {
                tmp = scanner.nextLine();

                //System.out.print(tmp);
                strarray = tmp.split("[,]+");
                //System.out.println(strarray[0]);
                lineVerticies[linecount] = new Vector3f(Float.parseFloat(strarray[1]), Float.parseFloat(strarray[2]), Float.parseFloat(strarray[3]));
                colors[linecount*4] = Float.parseFloat(strarray[4])/255f;
                colors[linecount*4+1] = Float.parseFloat(strarray[5])/255f;
                colors[linecount*4+2] = Float.parseFloat(strarray[6])/255f;
                colors[linecount*4+3] = 1f;
                
                normals[linecount*3] = Float.parseFloat(strarray[7]);
                normals[linecount*3+1] = Float.parseFloat(strarray[8]);
                normals[linecount*3+2] = Float.parseFloat(strarray[9]);
                
                // Colors where wrong, temp for figuring out geometry
                //colors[linecount*4] = 1f;
                //colors[linecount*4+1] = 1f;
                //colors[linecount*4+2] = 1f;
                //colors[linecount*4+3] = 1f;
                linecount++;
            }
            Mesh mesh = new Mesh();
            
            mesh.setMode(Mesh.Mode.Points);
            mesh.setPointSize(8f);  // this needs to be different depending on how close you are to the model. 
            mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
            mesh.setBuffer(Type.Color, 4, colors);
            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(lineVerticies));
            

            mesh.updateBound();
            mesh.updateCounts();

            Geometry geo = new Geometry("line", mesh);
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

            mat.setFloat("Shininess", 150f);
            mat.setBoolean("UseVertexColor", true);

            mat.setBoolean("UseMaterialColors", true);
            mat.setColor("Ambient", ColorRGBA.White); //Using white here, but shouldn’t matter that much
            mat.setColor("Diffuse", ColorRGBA.White);
            mat.setColor("Specular", ColorRGBA.White); //Using yellow for example
            mat.setBoolean("VertexLighting", true);


            // mat.setColor("Color", ColorRGBA.Cyan);
            //mat.setBoolean("VertexColor", true);
            //mat.setBoolean("UseMaterialColors", true);
            //mat.setColor("Ambient", ColorRGBA.White);
            //mat.setColor("Diffuse", ColorRGBA.White);
            //mat.setColor("Specular", ColorRGBA.White);
            //mat.setFloat("Shininess", 128f);  // [0,128]
            //mat.setBoolean("VertexLighting", true);
            geo.setMaterial(mat);



            core.attachChild(geo);



        } finally {
            scanner.close();
        }


    }

    @Override
    public void simpleInitApp() {

        /**
         * create a blue box at coordinates (1,-1,1)
         */
        Box box1 = new Box(1, 1, 1);
        Geometry blue = new Geometry("Box", box1);
        blue.setLocalTranslation(new Vector3f(0,0,0));
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        
        blue.setMaterial(mat1);
 
        /** create a red box straight above the blue one at (1,3,1) */

 
        /** Create a pivot node at (0,0,0) and attach it to the root node */
        pivot = new Node("pivot");
        rootNode.attachChild(pivot); // put this node in the scene
        pivot.scale(.07f);
        pivot.move(0,-10,-10);
        /** Attach the two boxes to the *pivot* node. (And transitively to the root node.) */
        pivot.attachChild(blue);

        /**
         * Rotate the pivot node: Note that both boxes have rotated!
         */
        PointLight sun = new PointLight();
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(.5f));
        rootNode.addLight(al);
        sun.setRadius(1000000000);
        
        sun.setColor(ColorRGBA.White.mult(4f));
       
        rootNode.addLight(sun);


        try {
            read(pivot);
        } catch (IOException ex) {
            Logger.getLogger(testjmonkey.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    
        /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate:
        pivot.rotate(0, 1*tpf, 0); 
    }
    
    private static class Files {

        public Files() {
        }
    }
}