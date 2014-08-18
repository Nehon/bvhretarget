/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package custom;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.app.Application;
import com.jme3.export.xml.XMLExporter;
import com.jme3.scene.plugins.bvh.SkeletonMapping;
import com.simsilica.lemur.event.BaseAppState;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3test.bvh.TestBVHReTarget;

/**
 *
 * @author Nehon
 */
public class MappingAppState extends BaseAppState {

    private SkeletonMapping mappings = new SkeletonMapping();
    private Skeleton targetSkeleton;
    private Skeleton sourceSkeleton;

    public MappingAppState(Skeleton targetSkeleton, Skeleton sourceSkeleton) {
        this.targetSkeleton = targetSkeleton;
        this.sourceSkeleton = sourceSkeleton;
    }

    @Override
    protected void initialize(Application aplctn) {
    }

    @Override
    protected void cleanup(Application aplctn) {
    }

    @Override
    protected void enable() {
    }

    @Override
    protected void disable() {
    }

    public SkeletonMapping getMappings() {
        return mappings;
    }

    public void addMapping(Map<Skeleton, Bone> mapping) {
        String targetBone = "";
        String sourceBone = "";
        for (Skeleton skeleton : mapping.keySet()) {
            if (skeleton == targetSkeleton) {
                targetBone = mapping.get(skeleton).getName();
            }
            if (skeleton == sourceSkeleton) {
                sourceBone = mapping.get(skeleton).getName();
            }
        }
        System.err.println(sourceBone + " -> " + targetBone);
        mappings.map(targetBone).addSourceBones(sourceBone);
    }
    
    public void saveMapping() {

        /**
         * Save a Node to a .xml file.
         */
        String userHome = System.getProperty("user.home");
        String filePath =  userHome + "/"+ System.currentTimeMillis() +"-mapping.xml";
        XMLExporter exporter = XMLExporter.getInstance();
        File file = new File(filePath);
        try {
            exporter.save(mappings, file);
            Logger.getLogger(MappingAppState.class.getName()).log(Level.INFO, "Mapping saved as {0}", filePath);
        } catch (IOException ex) {
            Logger.getLogger(MappingAppState.class.getName()).log(Level.SEVERE, "Failed to save node!", ex);
        }
    } 
}
