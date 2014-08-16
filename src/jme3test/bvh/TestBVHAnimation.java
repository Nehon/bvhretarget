/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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
package jme3test.bvh;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.scene.plugins.bvh.BVHAnimData;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.plugins.bvh.BVHLoader;
import com.jme3.util.BufferUtils;
import java.util.HashMap;

public class TestBVHAnimation extends SimpleApplication {

    public static void main(String[] args) {
        TestBVHAnimation app = new TestBVHAnimation();
        app.start();

    }

    @Override
    public void simpleInitApp() {
        final String animName = "ballerina";
        //final String animName = "85_07";


        assetManager.registerLoader(BVHLoader.class, "bvh","BVH");
        
        BVHAnimData animData = (BVHAnimData) assetManager.loadAsset("Animations/" + animName + ".bvh");
        initHud(animName + ".bvh");
        System.err.println(animData.getSkeleton());

        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", animData.getSkeleton());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
        rootNode.attachChild(skeletonDebug);

        Mesh[] meshes = new Mesh[2];
        meshes[0] = skeletonDebug.getWires();
        createBindPose(skeletonDebug.getWires());
        meshes[1] = skeletonDebug.getPoints();
        createBindPose(skeletonDebug.getPoints());

        HashMap<String, Animation> anims = new HashMap<String,Animation>();
        anims.put(animData.getAnimation().getName(), animData.getAnimation());

        AnimControl ctrl = new AnimControl(animData.getSkeleton());        
        ctrl.setAnimations(anims);
        skeletonDebug.addControl(ctrl);

        for (String anim : ctrl.getAnimationNames()) {
            System.out.println(anim);
        }

        AnimChannel channel = ctrl.createChannel();

        channel.setAnim(animName);
        channel.setLoopMode(LoopMode.Cycle);
        channel.setSpeed(1f);
        flyCam.setMoveSpeed(50);

        cam.setLocation(new Vector3f(45.214333f, 15.7383585f, 140.1217f));
        cam.setRotation(new Quaternion(4.7142492E-4f, 0.99905545f, 0.041977096f, -0.011220156f));
        
//        ChaseCamera chaser = new ChaseCamera(cam, inputManager);
//        skeletonDebug.addControl(chaser);



    }

    private void initHud(String text) {
        /** Write text on the screen (HUD) */
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText(text);
        helloText.setLocalTranslation(settings.getWidth() / 2 - helloText.getLineWidth() / 2, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);
    }

    private void createBindPose(Mesh mesh) {
        VertexBuffer pos = mesh.getBuffer(Type.Position);
        if (pos == null || mesh.getBuffer(Type.BoneIndex) == null) {
            // ignore, this mesh doesn't have positional data
            // or it doesn't have bone-vertex assignments, so its not animated
            return;
        }

        VertexBuffer bindPos = new VertexBuffer(Type.BindPosePosition);
        bindPos.setupData(Usage.CpuOnly,
                3,
                Format.Float,
                BufferUtils.clone(pos.getData()));
        mesh.setBuffer(bindPos);

        // XXX: note that this method also sets stream mode
        // so that animation is faster. this is not needed for hardware skinning
        pos.setUsage(Usage.Stream);

        VertexBuffer norm = mesh.getBuffer(Type.Normal);
        if (norm != null) {
            VertexBuffer bindNorm = new VertexBuffer(Type.BindPoseNormal);
            bindNorm.setupData(Usage.CpuOnly,
                    3,
                    Format.Float,
                    BufferUtils.clone(norm.getData()));
            mesh.setBuffer(bindNorm);
            norm.setUsage(Usage.Stream);
        }
    }
}
