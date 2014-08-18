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

import custom.SkeletonDebugAppState;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.scene.plugins.bvh.BVHAnimData;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.export.xml.XMLExporter;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.plugins.bvh.BVHLoader;
import com.jme3.scene.plugins.bvh.BVHUtils;
import com.jme3.util.BufferUtils;
import java.util.HashMap;
import static com.jme3.math.FastMath.*;
import static com.jme3.math.Vector3f.*;
import com.jme3.scene.plugins.bvh.SkeletonMapping;
import com.jme3.util.TempVars;
import custom.MappingAppState;
import custom.SkeletonDebugger;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3test.bvh.gui.GuiAppState;

public class TestBVHReTarget extends SimpleApplication implements AnimEventListener {

    public static void main(String[] args) {
        TestBVHReTarget app = new TestBVHReTarget();
        app.start();

    }
    private final String poseAnim = "IdleBase";

    @Override
    public void simpleInitApp() {
        SkeletonDebugAppState debugAppState = new SkeletonDebugAppState();
        stateManager.attach(debugAppState);

        viewPort.setBackgroundColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        // final String animName="37_01";
        assetManager.registerLoader(BVHLoader.class, "bvh", "BVH");
        final String animName = "ballerina";
        BVHAnimData animData = (BVHAnimData) assetManager.loadAsset("Animations/" + animName + ".bvh");
        initHud(animName + ".bvh");
        createLights();

        Node model = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        //    Node model = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");

        rootNode.attachChild(model);

        AnimControl control = model.getControl(AnimControl.class);

        float targetHeight = ((BoundingBox) model.getWorldBound()).getYExtent();//BVHUtils.getSkeletonHeight(control.getSkeleton());
        float sourceHeight = BVHUtils.getSkeletonHeight(animData.getSkeleton());
        float ratio = targetHeight / sourceHeight;
        System.out.println(((BoundingBox) model.getWorldBound()).getYExtent());
        final AnimChannel animChannel = createAnimSkeleton(animData, ratio, animName);

        System.out.println(animData.getSkeleton());

        SkeletonMapping skMap = new SkeletonMapping();
        //Sinbad - Ballerina
        skMap.map("Root", "Hips", PI, UNIT_Y);
        skMap.map("Stomach", "Chest", PI, UNIT_Y);
        skMap.map("Neck", "Neck", PI, UNIT_Y);
        skMap.map("Head", "Head", PI, UNIT_Y);
        skMap.map("Clavicle.L", "LeftCollar", new Quaternion().fromAngleAxis(-HALF_PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Clavicle.R", "RightCollar", new Quaternion().fromAngleAxis(HALF_PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Humerus.L", "LeftUpArm", new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Humerus.R", "RightUpArm", new Quaternion().fromAngleAxis(-PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Ulna.L", "LeftLowArm", new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(-HALF_PI, UNIT_Y)));
        skMap.map("Ulna.R", "RightLowArm", new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(HALF_PI, UNIT_Y)));
        skMap.map("Hand.L", "LeftHand", new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Hand.R", "RightHand", new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(HALF_PI, UNIT_Y)));
        skMap.map("Thigh.L", "LeftUpLeg", new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Thigh.R", "RightUpLeg", new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Calf.L", "LeftLowLeg", new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Calf.R", "RightLowLeg", new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Foot.L", "LeftFoot", new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        skMap.map("Foot.R", "RightFoot", new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Root", "Hips",PI,UNIT_Y);
        //        skMap.map("Stomach", "Chest",PI,UNIT_Y);
        //        skMap.map("Chest", "CS_BVH",PI,UNIT_Y);        
        //        skMap.map("Neck", "Neck",PI,UNIT_Y);
        //        skMap.map("Head", "Head",PI,UNIT_Y);
        //        skMap.map("Clavicle.L", "LeftCollar",new Quaternion().fromAngleAxis(-HALF_PI-HALF_PI/3f, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Clavicle.R", "RightCollar",new Quaternion().fromAngleAxis(HALF_PI+HALF_PI/3f, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Humerus.L", "LeftShoulder",new Quaternion().fromAngleAxis(PI+HALF_PI/5f, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Humerus.R", "RightShoulder",new Quaternion().fromAngleAxis(-PI-HALF_PI/5f, UNIT_Z).mult(new Quaternion().fromAngleAxis(-PI, UNIT_Y)));
        //        skMap.map("Ulna.L", "LeftElbow",new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(-HALF_PI, UNIT_Y)));
        //        skMap.map("Ulna.R", "RightElbow",new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(HALF_PI, UNIT_Y)));
        //        skMap.map("Hand.L", "LeftWrist",new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Hand.R", "RightWrist",new Quaternion().fromAngleAxis(PI, UNIT_Z).mult(new Quaternion().fromAngleAxis(HALF_PI, UNIT_Y)));
        //        skMap.map("Thigh.L", "LeftHip",new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Thigh.R", "RightHip",new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Calf.L", "LeftKnee",new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Calf.R", "RightKnee",new Quaternion().fromAngleAxis(PI, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Foot.L", "LeftAnkle",new Quaternion().fromAngleAxis(PI-HALF_PI/1.5f, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));
        //        skMap.map("Foot.R", "RightAnkle",new Quaternion().fromAngleAxis(PI-HALF_PI/1.5f, UNIT_X).mult(new Quaternion().fromAngleAxis(PI, UNIT_Y)));


  
        //   SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", control.getSkeleton(), assetManager, false);
        debugAppState.addSkeleton("SinbadSkeleton", control.getSkeleton(), false);

        //FIXME it shouldn't be model here as first argument.
        control.addAnim(BVHUtils.reTarget(model, model, animData.getAnimation(), animData.getSkeleton(), animData.getTimePerFrame(), skMap, false));

        final AnimChannel channel = control.createChannel();
        control.addListener(this);

        inputManager.addListener(new ActionListener() {
            public void onAction(String binding, boolean value, float tpf) {
                if (binding.equals(animName) && value) {
                    if (channel.getAnimationName() == null || !channel.getAnimationName().equals(animName)) {
                        channel.setAnim(animName, 0.50f);
                        channel.setLoopMode(LoopMode.Loop);
                        //    channel.setSpeed(0.5f);

                        animChannel.setAnim(animName, 0.50f);
                        animChannel.setLoopMode(LoopMode.Loop);
//                      //  animChannel.setSpeed(0.5f);
                    }
                }
            }
        }, animName);
        inputManager.addMapping(animName, new KeyTrigger(KeyInput.KEY_SPACE));
        setUpCamInput(model);

        GuiAppState state = new GuiAppState();
        stateManager.attach(state);
        
        MappingAppState mappingState = new MappingAppState(control.getSkeleton(), animData.getSkeleton());
        stateManager.attach(mappingState);
        
    }
    private boolean pan = false;

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
        /**
         * Write text on the screen (HUD)
         */
       // guiNode.detachAllChildren();
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        BitmapText helloText = new BitmapText(guiFont, false);
//        helloText.setSize(guiFont.getCharSet().getRenderedSize());
//        helloText.setText(text);
//        helloText.setLocalTranslation(settings.getWidth() / 2 - helloText.getLineWidth() / 2, helloText.getLineHeight(), 0);
//        guiNode.attachChild(helloText);
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
        //      SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", animData.getSkeleton(), assetManager, true);

        //  

        SkeletonDebugger skeletonDebug = stateManager.getState(SkeletonDebugAppState.class).addSkeleton("skeleton", animData.getSkeleton(), true);
        skeletonDebug.setLocalScale(scale);

        skeletonDebug.setLocalTranslation(7, 0, 0);

        HashMap<String, Animation> anims = new HashMap<String, Animation>();
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

    protected void setUpCamInput(Node model) {
        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, inputManager);
        chaseCam.setDefaultHorizontalRotation(HALF_PI);
        chaseCam.setMinVerticalRotation(-HALF_PI + 0.01f);
        chaseCam.setInvertVerticalAxis(true);


        final Node chaseCamTarget = new Node("ChaseCamTarget");
        rootNode.attachChild(chaseCamTarget);
        chaseCamTarget.addControl(chaseCam);
        chaseCamTarget.setLocalTranslation(model.getLocalTranslation());


        inputManager.addListener(new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("Pan")) {
                    if (isPressed) {
                        pan = true;
                    } else {
                        pan = false;
                    }
                }
            }
        }, "Pan");
        inputManager.addMapping("Pan", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addListener(new AnalogListener() {
            public void onAnalog(String name, float value, float tpf) {
                if (pan) {
                    value *= 10f;
                    TempVars vars = TempVars.get();
                    if (name.equals("mouseMoveDown")) {
                        chaseCamTarget.move(cam.getUp().mult(-value, vars.vect1));
                    }
                    if (name.equals("mouseMoveUp")) {
                        chaseCamTarget.move(cam.getUp().mult(value, vars.vect1));
                    }
                    if (name.equals("mouseMoveLeft")) {
                        chaseCamTarget.move(cam.getLeft().mult(value, vars.vect1));
                    }
                    if (name.equals("mouseMoveRight")) {
                        chaseCamTarget.move(cam.getLeft().mult(-value, vars.vect1));
                    }
                    vars.release();
                }
            }
        }, "mouseMoveDown", "mouseMoveUp", "mouseMoveLeft", "mouseMoveRight");
        inputManager.addMapping("mouseMoveDown", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("mouseMoveUp", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("mouseMoveLeft", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("mouseMoveRight", new MouseAxisTrigger(MouseInput.AXIS_X, true));

    }


}
