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
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.scene.plugins.bvh.BVHAnimData;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.plugins.bvh.BVHUtils;
import com.jme3.scene.plugins.bvh.SkeletonMapping;
import com.jme3.util.BufferUtils;
import java.util.HashMap;

public class TestAnimReTarget extends SimpleApplication implements AnimEventListener {

    public static void main(String[] args) {
        TestAnimReTarget app = new TestAnimReTarget();
        app.start();

    }

    @Override
    public void simpleInitApp() {
        createLights();
        final String animName = "anim";
        Node model = (Node) assetManager.loadModel("Models/Test/Cube1.j3o");
        Node model2 = (Node) assetManager.loadModel("Models/Test/Cube2.j3o");
     
        rootNode.attachChild(model);
        rootNode.attachChild(model2);
        Node camTarget = new Node();
        
        camTarget.move(0, 3, 0);
        rootNode.attachChild(camTarget);
        
        AnimControl control = model.getControl(AnimControl.class);
        AnimControl control2 = model2.getControl(AnimControl.class);
        Animation anim = control.getAnim(animName);
        System.err.println("Jaime skeleton height : "+BVHUtils.getSkeletonHeight(control2.getSkeleton()));
        System.err.println("Sinbad skeleton height : "+BVHUtils.getSkeletonHeight(control.getSkeleton()));
        
        float targetHeight = ((BoundingBox)model2.getWorldBound()).getYExtent();//BVHUtils.getSkeletonHeight(control.getSkeleton());
        float sourceHeight = ((BoundingBox)model.getWorldBound()).getYExtent();
        float ratio = sourceHeight / targetHeight;
        model.move(-3, 0, 0);
        model2.move(3, 0, 0);

        SkeletonMapping skMap = new SkeletonMapping();
        skMap.map("Root","Root");
        skMap.map("Bone1","Bone1", new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
        skMap.map("Bone2","Bone2", new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));

        control2.addAnim(BVHUtils.reTarget(model, model2, anim, control.getSkeleton(), skMap, false));

        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", control.getSkeleton());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
        rootNode.attachChild(skeletonDebug);
        skeletonDebug.setCullHint(Spatial.CullHint.Always);

        final AnimChannel channel = control.createChannel();
        control.addListener(this);
        final AnimChannel animChannel = control2.createChannel();

        inputManager.addListener(new ActionListener() {

            public void onAction(String binding, boolean value, float tpf) {
                if (binding.equals(animName) && value) {
                    if (channel.getAnimationName() == null || !channel.getAnimationName().equals(animName)) {
                        channel.reset(true);                        
                        channel.setAnim(animName, 0f);
                        channel.setLoopMode(LoopMode.Cycle);
                        channel.setSpeed(0.01f);

                        animChannel.setAnim(animName, 0f);
                        animChannel.setLoopMode(LoopMode.Cycle);
                        animChannel.setSpeed(0.01f);
                    }
                }
            }
        }, animName);
        inputManager.addMapping(animName, new KeyTrigger(KeyInput.KEY_SPACE));

        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
        chaseCam.setMaxDistance(300);
        chaseCam.setDefaultHorizontalRotation(FastMath.HALF_PI);
        chaseCam.setDefaultVerticalRotation(0);
        camTarget.addControl(chaseCam);

        rootNode.attachChild(createAxis("xAxis", Vector3f.UNIT_X.mult(3), ColorRGBA.Blue));
        rootNode.attachChild(createAxis("yAxis", Vector3f.UNIT_Y.mult(3), ColorRGBA.Red));
        rootNode.attachChild(createAxis("zAxis", Vector3f.UNIT_Z.mult(3), ColorRGBA.Green));

        flyCam.setMoveSpeed(50);
        cam.setLocation(new Vector3f(4.388378f, 1.4178623f, 15.287608f));
        cam.setRotation(new Quaternion(-0.010233912f, 0.9864035f, -0.06753517f, -0.1494735f));

    }

    private void createLights() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -0.7f, -1).normalizeLocal());
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(al);
    }

    private Geometry createAxis(String name, Vector3f extend, ColorRGBA color) {
        Arrow axis = new Arrow(extend);
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //   m.getAdditionalRenderState().setDepthWrite(false);
        m.setColor("Color", color);
        Geometry geo = new Geometry(name, axis);
        geo.setMaterial(m);
        return geo;
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

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
//        if (animName.equals("ballerina")) {
//            channel.setAnim(poseAnim, 0.50f);
//            channel.setLoopMode(LoopMode.Loop);
//            channel.setSpeed(1f);
//        }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

    private AnimChannel createAnimSkeleton(BVHAnimData animData, float scale, String animName) {
        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", animData.getSkeleton());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
        skeletonDebug.setLocalScale(scale);
        rootNode.attachChild(skeletonDebug);

        Mesh[] meshes = new Mesh[2];
        meshes[0] = skeletonDebug.getWires();
        createBindPose(skeletonDebug.getWires());
        meshes[1] = skeletonDebug.getPoints();
        createBindPose(skeletonDebug.getPoints());

        HashMap<String,Animation> anims = new HashMap<String,Animation>();
        anims.put(animData.getAnimation().getName(), animData.getAnimation());

        AnimControl ctrl = new AnimControl(animData.getSkeleton());
        ctrl.setAnimations(anims);
        skeletonDebug.addControl(ctrl);

        for (String anim : ctrl.getAnimationNames()) {
            System.out.println(anim);
        }

        ctrl.addListener(this);
        AnimChannel channel = ctrl.createChannel();

//        channel.setAnim(animName);
//        channel.setLoopMode(LoopMode.Cycle);
//        channel.setSpeed(1f);
        return channel;
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
