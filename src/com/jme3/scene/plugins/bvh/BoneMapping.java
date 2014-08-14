/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.scene.plugins.bvh;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author Nehon
 */
public class BoneMapping {
    private String sourceName;
    private Quaternion twist;

    public BoneMapping(String sourceName) {
        this.sourceName = sourceName;
        this.twist = new Quaternion();
    }

    public BoneMapping(String sourceName, Quaternion twist) {
        this.sourceName = sourceName;
        this.twist = twist;
    }
    
    public BoneMapping(String sourceName, float twistAngle, Vector3f twistAxis) {
        this.sourceName = sourceName;
        this.twist = new Quaternion().fromAngleAxis(twistAngle, twistAxis);
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Quaternion getTwist() {
        return twist;
    }

    public void setTwist(Quaternion twist) {
        this.twist = twist;
    }
    
    
}
