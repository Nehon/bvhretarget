/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package custom;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nehon
 */
public class SkeletonDebugAppState extends AbstractAppState{
    
    private Node debugNode= new Node("debugNode");
    
    private Map<Skeleton, SkeletonDebugger> skeletons = new HashMap<Skeleton, SkeletonDebugger>();
    private  Application app;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {        
        ViewPort vp = app.getRenderManager().createMainView("debug", app.getCamera());        
        vp.attachScene(debugNode);
        vp.setClearDepth(true);
        this.app = app;
        for (SkeletonDebugger skeletonDebugger : skeletons.values()) {
            skeletonDebugger.initialize(app.getAssetManager());
        }
        app.getInputManager().addListener(analogListener, "shoot");
        app.getInputManager().addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT),new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        debugNode.updateLogicalState(tpf);
        debugNode.updateGeometricState();
    }
    
    public SkeletonDebugger addSkeleton(String name, Skeleton skeleton, boolean guessBonesOrientation){
        SkeletonDebugger sd = new SkeletonDebugger(name, skeleton, guessBonesOrientation);
        skeletons.put(skeleton, sd);
        debugNode.attachChild(sd);
        if(isInitialized()){
            sd.initialize(app.getAssetManager());
        }
        return sd;
    }
    
     /** Pick a Target Using the Mouse Pointer. <ol><li>Map "pick target" action to a MouseButtonTrigger. <li>flyCam.setEnabled(false); <li>inputManager.setCursorVisible(true); <li>Implement action in AnalogListener (TODO).</ol>
 */
 private ActionListener analogListener = new ActionListener() {
   public void onAction(String name,  boolean isPressed,  float tpf) {
     if (name.equals("shoot") && isPressed) {       
       CollisionResults results = new CollisionResults();       
       Vector2f click2d = app.getInputManager().getCursorPosition();
       Vector3f click3d = app.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
       Vector3f dir = app.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
       Ray ray = new Ray(click3d, dir);
       
       debugNode.collideWith(ray, results);

       if (results.size() > 0) {
         // The closest result is the target that the player picked:
         Geometry target = results.getClosestCollision().getGeometry();
           for (SkeletonDebugger skeleton : skeletons.values()) {
               Bone selectedBone = skeleton.select(target);
               if(selectedBone !=null){
                   System.err.println("Selected Bone : " + selectedBone.getName() +" in skeleton "+skeleton.getName());
                   return;
               }
           }       
       }
     } 
   }
 }; 


    public Node getDebugNode() {
        return debugNode;
    }

    public void setDebugNode(Node debugNode) {
        this.debugNode = debugNode;
    }
    
    
    
}
