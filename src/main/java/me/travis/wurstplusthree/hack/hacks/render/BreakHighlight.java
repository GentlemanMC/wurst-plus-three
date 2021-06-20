package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.BlockBreakingEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Madmegsox1
 * @since 25/04/2021
 * fixed ur shit nerd ¬ travis
 */

@Hack.Registration(name = "Break Highlight", description = "highlights where people are breaking", category = Hack.Category.RENDER, priority = HackPriority.Lowest)
public class BreakHighlight extends Hack {

    ColourSetting self = new ColourSetting("Self Colour", new Colour(255,255,255, 200), this);
    ColourSetting friendColour = new ColourSetting("Friend Colour", new Colour(0,0,255, 200), this);
    ColourSetting other = new ColourSetting("Other Colour", new Colour(255, 0, 0), this);
    EnumSetting renderMode = new EnumSetting("Mode", "Both", Arrays.asList("Outline", "Both", "Fill"), this);
    HashMap<Integer, Pair<Integer, BlockPos>> breakingBlockList = new HashMap<>();

    @Override
    public void onEnable(){
        breakingBlockList.clear();
    }

    @CommitEvent(priority = EventPriority.LOW)
    public void damageBlockEvent(BlockBreakingEvent event){
        if (mc.world.getBlockState(event.pos).getBlock() == Blocks.BEDROCK) return;
        if(breakingBlockList.isEmpty()){
            breakingBlockList.putIfAbsent(event.breakingID, new Pair<>(event.breakStage, event.pos));
        } else {
            HashMap<Integer, Pair<Integer, BlockPos>> shitToAdd = new HashMap<>();
            List<Integer> idsToRemove = new ArrayList<>();
            for (int id : breakingBlockList.keySet()) {
                Pair<Integer, BlockPos> pair = breakingBlockList.get(id);
                if (event.breakingID != id) {
                    shitToAdd.put(event.breakingID, new Pair<>(event.breakStage, event.pos));
                } else {
                    if (event.breakStage > pair.getKey()) {
                        idsToRemove.add(event.breakingID);
                        shitToAdd.put(event.breakingID, new Pair<>(event.breakStage, event.pos));
                    }
                    if (event.breakingID == id && event.pos != pair.getValue()) {
                        idsToRemove.add(id);
                    }
                    if (event.breakingID == id && event.breakStage < pair.getKey()) {
                        idsToRemove.add(id);
                    }
                }
            }
            for (int id : idsToRemove) {
                breakingBlockList.remove(id);
            }
            breakingBlockList.putAll(shitToAdd);
        }
    }

    @Override
    public void onRender3D(Render3DEvent event){
        for(int i : breakingBlockList.keySet()){
            if(breakingBlockList.get(i).getValue() == null)continue;
            BlockPos pos = breakingBlockList.get(i).getValue();
            int state = breakingBlockList.get(i).getKey();
            EntityPlayer player = (EntityPlayer) mc.world.getEntityByID(i);
            if(pos != null && state != -1 && mc.world.getBlockState(pos).getBlock() != Blocks.AIR){
                AxisAlignedBB bb = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos);
                bb = calcBB(bb, state);
                if(player == mc.player){
                    switch (renderMode.getValue()){
                        case "Both":
                            RenderUtil.drawBBBox(bb, self.getValue(), self.getValue().getAlpha());
                            RenderUtil.drawBlockOutlineBB(bb, new Color(self.getValue().getRed(),self.getValue().getGreen(), self.getValue().getBlue(), 255), 1f);
                            break;
                        case "Outline":
                            RenderUtil.drawBlockOutlineBB(bb, self.getValue(), 1f);
                            break;
                        case "Fill":
                            RenderUtil.drawBBBox(bb, self.getValue(), self.getValue().getAlpha());
                            break;
                    };
                }else if(WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) {
                    switch (renderMode.getValue()){
                        case "Both":
                            RenderUtil.drawBBBox(bb, friendColour.getValue(), friendColour.getValue().getAlpha());
                            RenderUtil.drawBlockOutlineBB(bb, new Color(friendColour.getValue().getRed(), friendColour.getValue().getGreen(), friendColour.getValue().getBlue(), 255), 1f);
                            break;
                        case "Outline":
                            RenderUtil.drawBlockOutlineBB(bb, friendColour.getValue(), 1f);
                            break;
                        case "Fill":
                            RenderUtil.drawBBBox(bb, friendColour.getValue(), friendColour.getValue().getAlpha());
                            break;
                    }
                }else {
                    switch (renderMode.getValue()){
                        case "Both":
                            RenderUtil.drawBBBox(bb, other.getValue(), other.getValue().getAlpha());
                            RenderUtil.drawBlockOutlineBB(bb, new Color(other.getValue().getRed(), other.getValue().getGreen(), other.getValue().getBlue(), 255), 1f);
                            break;
                        case "Outline":
                            RenderUtil.drawBlockOutlineBB(bb, other.getValue(), 1f);
                            break;
                        case "Fill":
                            RenderUtil.drawBBBox(bb, other.getValue(), other.getValue().getAlpha());
                            break;
                    }
                }
            }
        }
    }


    private AxisAlignedBB calcBB(AxisAlignedBB bb, int state){
        AxisAlignedBB rbb = bb;
        switch (state){
            case 0:
                rbb = bb.shrink(0.6);
                break;
            case 1:
                rbb = bb.shrink(0.65);
                break;
            case 2:
                rbb = bb.shrink(0.7);
                break;
            case 3:
                rbb = bb.shrink(0.75);
                break;
            case 4:
                rbb = bb.shrink(0.8);
                break;
            case 5:
                rbb = bb.shrink(0.85);
                break;
            case 6:
                rbb = bb.shrink(0.9);
                break;
            case 7:
                rbb = bb.shrink(0.95);
                break;
            case 8:
                rbb = bb;
                break;
        }
        return rbb;
    }

    @Override
    public void onLogout() {
        breakingBlockList.clear();
    }
}
