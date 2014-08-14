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
package mygame;

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
import com.jme3.scene.plugins.bvh.BoneMapping;
import com.jme3.util.BufferUtils;
import java.util.HashMap;
import java.util.Map;
import static com.jme3.math.FastMath.*;
import static com.jme3.math.Vector3f.*;



public class TestAnimReTarget1 extends SimpleApplication implements AnimEventListener {

    public static void main(String[] args) {
        TestAnimReTarget1 app = new TestAnimReTarget1();
        app.start();

    }
   

    @Override
    public void simpleInitApp() {
        createLights();
       //final String animName = "anim";
         final String animName = "Dance";
        Node model = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        Node model2 = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
//        Node model = (Node) assetManager.loadModel("Models/Test/Cube1.j3o");
//        Node model2 = (Node) assetManager.loadModel("Models/Test/Cube2.j3o");
    //    Node model = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");

     
        rootNode.attachChild(model);
        rootNode.attachChild(model2);
        Node camTarget = new Node();
        
        camTarget.move(0, 3, 0);
        rootNode.attachChild(camTarget);
        
        AnimControl control = model.getControl(AnimControl.class);
        AnimControl control2 = model2.getControl(AnimControl.class);
        Animation anim = control.getAnim(animName);
        //System.err.println(control2.getSkeleton());
        System.err.println("Jaime skeleton height : "+BVHUtils.getSkeletonHeight(control2.getSkeleton()));
        System.err.println("Sinbad skeleton height : "+BVHUtils.getSkeletonHeight(control.getSkeleton()));
        
        float targetHeight = ((BoundingBox)model2.getWorldBound()).getYExtent();//BVHUtils.getSkeletonHeight(control.getSkeleton());
        float sourceHeight = ((BoundingBox)model.getWorldBound()).getYExtent();
        float ratio = sourceHeight / targetHeight;
        model.move(-3, sourceHeight+0.7f, 0);
      //  model.move(-3, 0, 0);
        model2.move(3, 0, 0);
        model2.scale(ratio/1.5f);
      //  System.out.println(((BoundingBox)model.getWorldBound()).getYExtent());
        //final AnimChannel animChannel = createAnimSkeleton(animData, ratio, animName);

        Map<String, BoneMapping> boneMapping = new HashMap<String, BoneMapping>();
        //Sinbad
        boneMapping.put("Root",new BoneMapping("Root"));
        boneMapping.put("hips",new BoneMapping("Waist"));
        boneMapping.put("spine",new BoneMapping("Stomach",PI,UNIT_Y));
        boneMapping.put("ribs",new BoneMapping("Chest",PI,UNIT_Y));
        boneMapping.put("neck",new BoneMapping("Neck",PI,UNIT_Y));
        boneMapping.put("head",new BoneMapping("Head",PI,UNIT_Y));
        boneMapping.put("shoulder.L",new BoneMapping("Clavicle.L"));
        boneMapping.put("shoulder.R",new BoneMapping("Clavicle.R"));
        boneMapping.put("upper_arm.L",new BoneMapping("Humerus.L",PI,UNIT_Y));
        boneMapping.put("upper_arm.R",new BoneMapping("Humerus.R",PI,UNIT_Y));
        boneMapping.put("forearm.L",new BoneMapping("Ulna.L"));
        boneMapping.put("forearm.R",new BoneMapping("Ulna.R"));
        boneMapping.put("hand.L",new BoneMapping("Hand.L",PI,UNIT_Y));
        boneMapping.put("hand.R",new BoneMapping("Hand.R",PI,UNIT_Y));
        boneMapping.put("thigh.L",new BoneMapping("Thigh.L",PI,UNIT_Y));
        boneMapping.put("thigh.R",new BoneMapping("Thigh.R",PI,UNIT_Y));
        boneMapping.put("shin.L",new BoneMapping("Calf.L",PI,UNIT_Y));
        boneMapping.put("shin.R",new BoneMapping("Calf.R",PI,UNIT_Y));
        boneMapping.put("foot.L",new BoneMapping("Foot.L"));
        boneMapping.put("foot.R",new BoneMapping("Foot.R"));
        
         boneMapping.put("thumb.01.L",new BoneMapping("ThumbProx.L"));
         boneMapping.put("thumb.02.L",new BoneMapping("ThumbMed.L"));
         boneMapping.put("thumb.03.L",new BoneMapping("ThumbDist.L"));
         
         boneMapping.put("finger_index.01.L",new BoneMapping("IndexFingerProx.L"));
         boneMapping.put("finger_index.02.L",new BoneMapping("IndexFingerMed.L"));
         boneMapping.put("finger_index.03.L",new BoneMapping("IndexFingerDist.L"));
         
         boneMapping.put("finger_middle.01.L",new BoneMapping("MiddleFingerProx.L"));
         boneMapping.put("finger_middle.02.L",new BoneMapping("MiddleFingerMed.L"));
         boneMapping.put("finger_middle.03.L",new BoneMapping("MiddleFingerDist.L"));
         
         boneMapping.put("finger_ring.01.L",new BoneMapping("RingFingerProx.L"));
         boneMapping.put("finger_ring.02.L",new BoneMapping("RingFingerMed.L"));
         boneMapping.put("finger_ring.03.L",new BoneMapping("RingFingerDist.L"));
         
         boneMapping.put("finger_pinky.01.L",new BoneMapping("PinkyProx.L"));
         boneMapping.put("finger_pinky.02.L",new BoneMapping("PinkyMed.L"));
         boneMapping.put("finger_pinky.03.L",new BoneMapping("PinkyDist.L"));
         
         boneMapping.put("thumb.01.R",new BoneMapping("ThumbProx.R"));
         boneMapping.put("thumb.02.R",new BoneMapping("ThumbMed.R"));
         boneMapping.put("thumb.03.R",new BoneMapping("ThumbDist.R"));
         
         boneMapping.put("finger_index.01.R",new BoneMapping("IndexFingerProx.R"));
         boneMapping.put("finger_index.02.R",new BoneMapping("IndexFingerMed.R"));
         boneMapping.put("finger_index.03.R",new BoneMapping("IndexFingerDist.R"));
         
         boneMapping.put("finger_middle.01.R",new BoneMapping("MiddleFingerProx.R"));
         boneMapping.put("finger_middle.02.R",new BoneMapping("MiddleFingerMed.R"));
         boneMapping.put("finger_middle.03.R",new BoneMapping("MiddleFingerDist.R"));
         
         boneMapping.put("finger_ring.01.R",new BoneMapping("RingFingerProx.R"));
         boneMapping.put("finger_ring.02.R",new BoneMapping("RingFingerMed.R"));
         boneMapping.put("finger_ring.03.R",new BoneMapping("RingFingerDist.R"));
         
         boneMapping.put("finger_pinky.01.R",new BoneMapping("PinkyProx.R"));
         boneMapping.put("finger_pinky.02.R",new BoneMapping("PinkyMed.R"));
         boneMapping.put("finger_pinky.03.R",new BoneMapping("PinkyDist.R"));
        
         
         
    //     boneMapping.put("jaw",new BoneMapping("Jaw",PI,UNIT_Y));
//         boneMapping.put("Cheek.R",new BoneMapping("Cheek.R"));
//         boneMapping.put("Cheek.L",new BoneMapping("Cheek.L"));
////         
//         boneMapping.put("LipBottom.L",new BoneMapping("LowerLip"));
//         boneMapping.put("LipTop.L",new BoneMapping("UpperLip"));
////         
//         boneMapping.put("LipBottom.R",new BoneMapping("LowerLip"));
//         boneMapping.put("LipTop.R",new BoneMapping("UpperLip"));
////        
//         boneMapping.put("eyebrow.02.R",new BoneMapping("Brow.R"));
//          boneMapping.put("eyebrow.02.L",new BoneMapping("Brow.L"));    
//          
//          boneMapping.put("eyebrow.01.R",new BoneMapping("Brow.C"));
//          boneMapping.put("eyebrow.01.L",new BoneMapping("Brow.C"));         
//         
//         boneMapping.put("eye.L",new BoneMapping("Eye.L"));
//         boneMapping.put("eye.R",new BoneMapping("Eye.R"));
         
         
         
//------
//---------- bone
//----------- bone
                 

//        boneMapping.put("Root","Root");
//        boneMapping.put("Bone1","Bone1");
//        boneMapping.put("Bone2","Bone2");
//-hips bone
//--pelvis bone
//---tail.001 bone
//----tail.002 bone
//-----tail.003 bone
//------tail.004 bone
//-------tail.005 bone
//--------tail.006 bone
//---------tail.007 bone
//----------tail.008 bone
//-----------tail.009 bone
//------------tail.010 bone
//---thigh.L bone
//----shin.L bone
//-----foot.L bone
//------toe.L bone
//---thigh.R bone
//----shin.R bone
//-----foot.R bone
//------toe.R bone
//--spine bone
//---ribs bone
//----neck bone
//-----head bone
//------IKjawTarget bone
//------jaw bone
//------LipBottom.R bone
//------LipTop.R bone
//------LipSide.R bone
//------eyebrow.01.R bone
//------eyebrow.02.R bone
//------eyebrow.03.R bone
//------Cheek.R bone
//------LipBottom.L bone
//------LipTop.L bone
//------LipSide.L bone
//------eyebrow.01.L bone
//------eyebrow.02.L bone
//------eyebrow.03.L bone
//------Cheek.L bone
//------EyeLidTop.R bone
//------EyeLidBottom.R bone
//------EyeLidTop.L bone
//------EyeLidBottom.L bone
//------SightTarget bone
//-------IKeyeTarget.R bone
//-------IKeyeTarget.L bone
//------eye.L bone
//------eye.R bone
//----shoulder.L bone
//-----upper_arm.L bone
//------forearm.L bone
//-------hand.L bone
//--------palm.01.L bone
//---------finger_index.01.L bone
//----------finger_index.02.L bone
//-----------finger_index.03.L bone
//---------thumb.01.L bone
//----------thumb.02.L bone
//-----------thumb.03.L bone
//--------palm.04.L bone
//---------finger_pinky.01.L bone
//----------finger_pinky.02.L bone
//-----------finger_pinky.03.L bone
//--------palm.02.L bone
//---------finger_middle.01.L bone
//----------finger_middle.02.L bone
//-----------finger_middle.03.L bone
//--------palm.03.L bone
//---------finger_ring.01.L bone
//----------finger_ring.02.L bone
//-----------finger_ring.03.L bone
//----shoulder.R bone
//-----upper_arm.R bone
//------forearm.R bone
//-------hand.R bone
//--------palm.01.R bone
//---------finger_index.01.R bone
//----------finger_index.02.R bone
//-----------finger_index.03.R bone
//---------thumb.01.R bone
//----------thumb.02.R bone
//-----------thumb.03.R bone
//--------palm.04.R bone
//---------finger_pinky.01.R bone
//----------finger_pinky.02.R bone
//-----------finger_pinky.03.R bone
//--------palm.02.R bone
//---------finger_middle.01.R bone
//----------finger_middle.02.R bone
//-----------finger_middle.03.R bone
//--------palm.03.R bone
//---------finger_ring.01.R bone
//----------finger_ring.02.R bone
//-----------finger_ring.03.R bone
//-IKheel.L bone
//-IKheel.R bone


        control2.addAnim(BVHUtils.reTarget(model, model2, anim, control.getSkeleton(), boneMapping, false));
//        
//        Animation anim2 = control.getAnim("RunTop");
//        control2.addAnim(BVHUtils.reTarget(model, model2, anim2, control.getSkeleton(), boneMapping, false));

        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", control.getSkeleton());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
        rootNode.attachChild(skeletonDebug);
        skeletonDebug.setCullHint(Spatial.CullHint.Always);

        final AnimChannel channel = control.createChannel();
        final AnimChannel channel2 = control.createChannel();
        channel2.addFromRootBone("Waist");
        control.addListener(this);
        final AnimChannel animChannel = control2.createChannel();
        final AnimChannel animChannel2 = control2.createChannel();
        animChannel2.addFromRootBone("spine");

        inputManager.addListener(new ActionListener() {

            public void onAction(String binding, boolean value, float tpf) {
                if (binding.equals(animName) && value) {
                    if (channel.getAnimationName() == null || !channel.getAnimationName().equals(animName)) {
                        channel.reset(true);                        
                        channel.setAnim(animName, 0f);
                        channel.setLoopMode(LoopMode.Loop);
                        channel.setSpeed(1f);
                        
//                        channel2.setAnim("RunTop", 0f);
//                        channel2.setLoopMode(LoopMode.Loop);
//                        channel2.setSpeed(1f);

                        animChannel.setAnim(animName, 0f);
                        animChannel.setLoopMode(LoopMode.Loop);
                        animChannel.setSpeed(1f);
//                        
//                        animChannel2.setAnim("RunTop", 0f);
//                        animChannel2.setLoopMode(LoopMode.Loop);
//                        animChannel2.setSpeed(1f);
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