/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.bvh.gui;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.event.BaseAppState;
import custom.MappingAppState;
import custom.SkeletonDebugAppState;

/**
 *
 * @author Nehon
 */
public class GuiAppState extends BaseAppState {

    @Override
    protected void initialize(final Application app) {
        GuiGlobals.initialize(app);
        Node guiNode = (Node)app.getGuiViewPort().getScenes().get(0);
       // BorderLayout layout = new BorderLayout();
        
        Button btnMap = new Button("Map bones");
        btnMap.addClickCommands(new Command<Button>() {

            public void execute(Button s) {
                System.err.println("mappppingggg");
                MappingAppState mapping = getStateManager().getState(MappingAppState.class);
                SkeletonDebugAppState skDebug = getStateManager().getState(SkeletonDebugAppState.class);
                mapping.addMapping(skDebug.getSelectedBones());
            }
        });       
        
        Button btnSave = new Button("Save mapping");
        btnSave.addClickCommands(new Command<Button>() {

            public void execute(Button s) {
                System.err.println("save");
                getStateManager().getState(MappingAppState.class).saveMapping();
            }
        });       
                
        btnMap.setPreferredSize(new Vector3f(100, 100, 1));
        btnMap.setLocalTranslation(0, app.getCamera().getHeight(), 1);
        btnSave.setPreferredSize(new Vector3f(100, 100, 1));
        btnSave.setLocalTranslation(100, app.getCamera().getHeight(), 1);
        //layout.addChild(BorderLayout.Position.South, btnMap);
        guiNode.attachChild(btnMap);
        guiNode.attachChild(btnSave);
        //btnMap.setLocalTranslation(new Vector3f(app.getCamera().getW, y, z));
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
    
}
