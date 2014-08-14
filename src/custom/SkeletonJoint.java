package custom;

/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.HashMap;
import java.util.Map;

/**
 * The class that displays either heads of the bones if no length data is
 * supplied or both heads and tails otherwise.
 */
public class SkeletonJoint extends Node {

    /**
     * The skeleton to be displayed.
     */
    private Skeleton skeleton;
    /**
     * The map between the bone index and its length.
     */
    private Map<Bone, Node> boneNodes = new HashMap<Bone, Node>();

  

    /**
     * Creates a points with bone lengths data. If the data is supplied then the
     * points will show both head and tail of each bone.
     *
     * @param skeleton the skeleton that will be shown
     * @param boneLengths a map between the bone's index and the bone's length
     */
    public SkeletonJoint(Skeleton skeleton, Map<Integer, Float> boneLengths, boolean guessBonesOrientation) {
        this.skeleton = skeleton;
        this.skeleton.reset();
        this.skeleton.updateWorldVectors();

        Sphere s = new Sphere(10, 10, 0.1f);

        for (Bone bone : skeleton.getRoots()) {
            createSkeletonGeoms(bone, s, boneLengths, skeleton, this, guessBonesOrientation);
        }

    }

    protected final void createSkeletonGeoms(Bone bone, Mesh jointShape, Map<Integer, Float> boneLengths, Skeleton skeleton, Node parent, boolean guessBonesOrientation) {
        Node n = new Node(bone.getName() + "JointNode");
        Geometry jGeom = new Geometry(bone.getName()+ "Joint", jointShape);
        n.setLocalTranslation(bone.getLocalPosition());
        n.setLocalRotation(bone.getLocalRotation());
        n.setLocalScale(bone.getLocalScale());

        float boneLength = boneLengths.get(skeleton.getBoneIndex(bone));
        jGeom.setLocalScale(boneLength);
        n.attachChild(jGeom);

        jGeom.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X).normalizeLocal());
      
        if (guessBonesOrientation) {
            //One child only, the bone direction is from the parent joint to the child joint.
            if (bone.getChildren().size() == 1) {
                Vector3f v = bone.getChildren().get(0).getLocalPosition();
                Quaternion q = new Quaternion();
                q.lookAt(v, Vector3f.UNIT_Z);
                jGeom.setLocalRotation(q);
            }
            //no child, the bone has the same direction as the parent bone.
            if (bone.getChildren().isEmpty()) {
                jGeom.setLocalRotation(parent.getChild(0).getLocalRotation());
            }
        }

        //tip
        if (bone.getChildren().size() != 1) {
            Geometry gt = jGeom.clone();
            Vector3f v = new Vector3f(0, boneLength, 0);
            if (guessBonesOrientation) {
                if (bone.getChildren().isEmpty()) {
                    gt.setLocalTranslation(jGeom.getLocalRotation().mult(parent.getChild(0).getLocalRotation()).mult(v,v));
                }
            } else {
                gt.setLocalTranslation(v);
            }

            n.attachChild(gt);
        }



        boneNodes.put(bone, n);
        parent.attachChild(n);
        for (Bone childBone : bone.getChildren()) {
            createSkeletonGeoms(childBone, jointShape, boneLengths, skeleton, n, guessBonesOrientation);
        }
    }

    protected final void updateSkeletonGeoms(Bone bone) {
        Node n = boneNodes.get(bone);
        n.setLocalTranslation(bone.getLocalPosition());
        n.setLocalRotation(bone.getLocalRotation());
        n.setLocalScale(bone.getLocalScale());

        for (Bone childBone : bone.getChildren()) {
            updateSkeletonGeoms(childBone);
        }
    }

    /**
     * The method updates the geometry according to the positions of the bones.
     */
    public void updateGeometry() {
        for (Bone bone : skeleton.getRoots()) {
            updateSkeletonGeoms(bone);
        }
    }
}
