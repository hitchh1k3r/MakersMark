package com.hitchh1k3rsguide.makersmark.asm;

import com.hitchh1k3rsguide.$CORE_REPLACE$.hitchcore.CoreUtils;
import com.hitchh1k3rsguide.makersmark.MakersMark;
import com.hitchh1k3rsguide.makersmark.util.Utils;
import net.minecraft.inventory.Container;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraftforge.classloading.FMLForgePlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASMTransformer implements IClassTransformer
{

    private static boolean TEST_MODE     = false;
    public static  int     failureStatus = 0;
    public static  String  patchFailures = "";

    @Override
    public byte[] transform(String className, String newName, byte[] bytecode)
    {
        if (newName.equals("net.minecraft.tileentity.TileEntityBrewingStand"))
        {
            if (TEST_MODE)
            {
                Utils.debugMsg("PATCHING TileEntityBrewingStand ::::::::::::::::::::::::::::::::::::::::::::::::::");
            }
            return patchBrewingStandTE(bytecode, !FMLForgePlugin.RUNTIME_DEOBF);
        }
        else if (newName.equals("net.minecraft.network.NetHandlerPlayServer"))
        {
            if (TEST_MODE)
            {
                Utils.debugMsg("PATCHING NetHandlerPlayServer ::::::::::::::::::::::::::::::::::::::::::::::::::");
            }
            return patchNetHandlerPlayServer(bytecode, !FMLForgePlugin.RUNTIME_DEOBF);
        }
        else if (newName.equals("net.minecraft.inventory.Container"))
        {
            if (TEST_MODE)
            {
                Utils.debugMsg("PATCHING Container ::::::::::::::::::::::::::::::::::::::::::::::::::");
            }
            return patchContainer(bytecode, !FMLForgePlugin.RUNTIME_DEOBF);
        }
        return bytecode;
    }

    private static boolean didTransform;

    public static void doPatches()
    {
        Class t;
        t = TileEntityBrewingStand.class;
        t = NetHandlerPlayServer.class;
        t = Container.class;

        if (failureStatus > 0)
        {
            CoreUtils.failedTransform(MakersMark.MODNAME, MakersMark.VERSION, patchFailures, failureStatus, MakersMark.LOGGER, MakersMark.MODID);
        }
    }

    private byte[] patchContainer(byte[] bytecode, boolean isDeobfuscated)
    {
        // net.minecraft.inventory.Container

        // NOTE this method patches vanilla minecraft (and doesn't need to be compatible)
        // if this fails leather bag recipes can break consuming more (stackable) objects
        // then are actually saved in the bag item

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        // Patch Target Method:
        MethodNode slotClick = getMethod(isDeobfuscated ? "slotClick" : "func_75144_a", "(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", classNode);

        didTransform = false;
        if (slotClick != null)
        {
            // Get Local Variable Indices:
            int local_l1 = -1;
            int local_slot2 = -1;
            int local_itemstack3 = -1;
            for (LocalVariableNode var : slotClick.localVariables)
            {
                if ("l1".equals(var.name) && "I".equals(var.desc))
                {
                    local_l1 = var.index;
                }
                else if ("slot2".equals(var.name) && "Lnet/minecraft/inventory/Slot;".equals(var.desc))
                {
                    local_slot2 = var.index;
                }
                else if ("itemstack3".equals(var.name) && "Lnet/minecraft/item/ItemStack;".equals(var.desc))
                {
                    local_itemstack3 = var.index;
                }
            }

            if (local_l1 > 0 && local_slot2 > 0 && local_itemstack3 > 0)
            {
                // Patch Target ASM:
                InsnList oldInstructions = new InsnList();
                oldInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", isDeobfuscated ? "stackSize" : "field_77994_a", "I"));
                oldInstructions.add(new VarInsnNode(Opcodes.ILOAD, local_l1));
                oldInstructions.add(new InsnNode(Opcodes.IADD));
                oldInstructions.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/item/ItemStack", isDeobfuscated ? "stackSize" : "field_77994_a", "I"));

                for (AbstractInsnNode node : findNodes(slotClick, oldInstructions))
                {
                    if (node != null)
                    {
                        node = node.getNext().getNext().getNext().getNext();

                        if (node instanceof JumpInsnNode)
                        {
                            // Patch New ASM:
                            InsnList newInstructions = new InsnList();
                            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, local_slot2));
                            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, local_itemstack3));
                            newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/inventory/Slot", isDeobfuscated ? "putStack" : "func_75215_d", "(Lnet/minecraft/item/ItemStack;)V", false));

                            // Do Patch:
                            if (TEST_MODE)
                            {
                                Utils.debugMsg("INSERTING INSTRUCTIONS!!!");
                            }
                            didTransform = true;
                            slotClick.instructions.insertBefore(node, newInstructions);
                        }
                    }
                }
            }
        }
        else if (TEST_MODE)
        {
            Utils.debugErr("Could not find method...");
        }

        if (!didTransform)
        {
            if (failureStatus < 1)
            {
                failureStatus = 1;
            }
            patchFailures += "<br />" + '\u00A0' + "- Container :: <em>prevents leather bag crafting from occasionally consuming more items then are actually put in the bag</em>";
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchNetHandlerPlayServer(byte[] bytecode, boolean isDeobfuscated)
    {
        // net.minecraft.network.NetHandlerPlayServer

        // NOTE adds a way to prevent player kicking (for flying), and is compatible with similar patches
        // if this fails players using potion of levitation can be kicked on servers (configuration can fix it)

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        // Patch Target Method:
        MethodNode processPlayer = getMethod(isDeobfuscated ? "processPlayer" : "func_147347_a", "(Lnet/minecraft/network/play/client/C03PacketPlayer;)V", classNode);

        didTransform = false;
        if (processPlayer != null)
        {
            // Patch Target ASM:
            InsnList oldInstructions = new InsnList();
            oldInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", isDeobfuscated ? "isFlightAllowed" : "func_71231_X", "()Z", false));

            for (AbstractInsnNode node : findNodes(processPlayer, oldInstructions))
            {
                if (node != null)
                {
                    node = node.getNext();

                    if (node instanceof JumpInsnNode)
                    {
                        LabelNode label = ((JumpInsnNode) node).label;

                        // Patch New ASM:
                        InsnList newInstructions = new InsnList();
                        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", isDeobfuscated ? "isFlightAllowed" : "func_71231_X", "()Z", false));
                        newInstructions.add(new JumpInsnNode(Opcodes.IFNE, label));
                        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/NetHandlerPlayServer", isDeobfuscated ? "playerEntity" : "field_147369_b", "Lnet/minecraft/entity/player/EntityPlayerMP;"));
                        newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/hitchh1k3rsguide/makersmark/util/HookUtils", "isFlightKickDisabled", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Z", false));

                        // Do Patch:
                        replaceInstruction(processPlayer, oldInstructions, newInstructions);
                    }
                }
            }
        }
        else if (TEST_MODE)
        {
            Utils.debugErr("Could not find method...");
        }

        if (!didTransform)
        {
            if (failureStatus < 1)
            {
                failureStatus = 1;
            }
            patchFailures += "<br />" + '\u00A0' + "- NetHandlerPlayServer :: <em>prevents servers from kicking players while using the potion of levitation</em>";
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchBrewingStandTE(byte[] bytecode, boolean isDeobfuscated)
    {
        // net.minecraft.tileentity.TileEntityBrewingStand

        // NOTE (this shouldn't ever fail, unless canBrew is missing) adds hook to the top of the canBrew method to allow for external inspection of brew stacks before brewing!

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        // Patch Target Method:
        MethodNode canBrew = getMethod(isDeobfuscated ? "canBrew" : "func_145934_k", "()Z", classNode);

        if (canBrew != null)
        {
            // Patch New ASM (canBrew):
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/TileEntityBrewingStand", isDeobfuscated ? "brewingItemStacks" : "field_145945_j", "[Lnet/minecraft/item/ItemStack;"));
            newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/hitchh1k3rsguide/makersmark/util/HookUtils", "canBrew", "([Lnet/minecraft/item/ItemStack;)Z", false));
            Label l1 = new Label();
            LabelNode l1n = new LabelNode(l1);
            newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, l1n));
            Label l2 = new Label();
            LabelNode l2n = new LabelNode(l2);
            newInstructions.add(l2n);
            newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/hitchh1k3rsguide/makersmark/util/HookUtils", "canBrewResult", "Z"));
            newInstructions.add(new InsnNode(Opcodes.IRETURN));
            newInstructions.add(l1n);
            newInstructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

            // Do Patch (canBrew):
            canBrew.instructions.insertBefore(canBrew.instructions.get(3), newInstructions);
        }
        else
        {
            if (TEST_MODE)
            {
                Utils.debugErr("Could not find method...");
            }

            if (failureStatus < 1)
            {
                failureStatus = 1;
            }
            patchFailures += "<br />" + '\u00A0' + "- TileEntityBrewingStand :: <em>adds a canBrew hook to allow for custom potions</em>";
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    /////////////////////////////////////////////////////
    ///////////////////// UTILITIES /////////////////////
    /////////////////////////////////////////////////////

    private void replaceInstruction(MethodNode method, InsnList target, InsnList replace)
    {
        replaceInstruction(method, target, replace, 0);
    }

    private void replaceInstruction(MethodNode method, InsnList target, InsnList replace, int skip)
    {
        if (method != null)
        {
            AbstractInsnNode instruction = method.instructions.getFirst();
            while (instruction != null)
            {
                AbstractInsnNode next = instruction.getNext();
                if (compareBlocks(instruction, target))
                {
                    if (TEST_MODE)
                    {
                        Utils.debugMsg("REPLACING INSTRUCTIONS!!!");
                    }
                    didTransform = true;
                    Iterator<AbstractInsnNode> it = replace.iterator();
                    boolean kill = false;
                    while (it.hasNext())
                    {
                        AbstractInsnNode node = it.next();
                        try
                        {
                            method.instructions.insertBefore(instruction, node.clone(null));
                        }
                        catch (Exception e)
                        {
                            method.instructions.insertBefore(instruction, node);
                            kill = true;
                        }
                    }
                    for (int i = 0; i < target.size() + skip; ++i)
                    {
                        method.instructions.remove(instruction);
                        instruction = next;
                        next = instruction.getNext();
                    }
                    if (kill)
                    {
                        return;
                    }
                }
                instruction = next;
            }
        }
        else if (TEST_MODE)
        {
            Utils.debugErr("Could not find method...");
        }
    }

    private List<AbstractInsnNode> findNodes(MethodNode method, InsnList search)
    {
        List<AbstractInsnNode> out = new ArrayList<AbstractInsnNode>();
        if (method != null)
        {
            AbstractInsnNode instruction = method.instructions.getFirst();
            while (instruction != null)
            {
                if (compareBlocks(instruction, search))
                {
                    out.add(instruction);
                }
                instruction = instruction.getNext();
            }
        }
        else if (TEST_MODE)
        {
            Utils.debugErr("Could not find method...");
        }
        return out;
    }

    private boolean compareNodes(AbstractInsnNode instruction, AbstractInsnNode compare)
    {
        if (instruction.getOpcode() == compare.getOpcode()
            && instruction.getClass().equals(compare.getClass()))
        {
            if (instruction instanceof FrameNode && compare instanceof FrameNode)
            {
                if (((FrameNode) instruction).type == ((FrameNode) compare).type
                    && ((FrameNode) instruction).local.equals(((FrameNode) compare).local)
                    && ((((FrameNode) instruction).stack == null && ((FrameNode) compare).stack == null) || ((FrameNode) instruction).stack
                        .equals(((FrameNode) compare).stack)))
                {
                    return true;
                }
            }
            else if (instruction instanceof VarInsnNode && compare instanceof VarInsnNode)
            {
                if (((VarInsnNode) instruction).var == ((VarInsnNode) compare).var)
                {
                    return true;
                }
            }
            else if (instruction instanceof MethodInsnNode && compare instanceof MethodInsnNode)
            {
                if (((MethodInsnNode) instruction).desc.equals(((MethodInsnNode) compare).desc)
                    && ((MethodInsnNode) instruction).name
                            .equals(((MethodInsnNode) compare).name)
                    && ((MethodInsnNode) instruction).owner
                            .equals(((MethodInsnNode) compare).owner)
                    && ((MethodInsnNode) instruction).itf == ((MethodInsnNode) compare).itf)
                {
                    return true;
                }
            }
            else if (instruction instanceof FieldInsnNode && compare instanceof FieldInsnNode)
            {
                if (((FieldInsnNode) instruction).desc.equals(((FieldInsnNode) compare).desc)
                    && ((FieldInsnNode) instruction).name
                            .equals(((FieldInsnNode) compare).name)
                    && ((FieldInsnNode) instruction).owner
                            .equals(((FieldInsnNode) compare).owner))
                {
                    return true;
                }
            }
            else if (instruction instanceof InsnNode && compare instanceof InsnNode)
            {
                // only has opcode (and we already checked if those match)
                return true;
            }
            else if (instruction instanceof LdcInsnNode && compare instanceof LdcInsnNode)
            {
                if (((LdcInsnNode) instruction).cst.equals(((LdcInsnNode) compare).cst))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compareBlocks(AbstractInsnNode instruction, InsnList target)
    {
        Iterator<AbstractInsnNode> it = target.iterator();
        while (it.hasNext())
        {
            AbstractInsnNode compare = it.next();
            if (!compareNodes(instruction, compare))
            {
                return false;
            }
            instruction = instruction.getNext();
            if (instruction == null)
            {
                return false;
            }
        }
        return true;
    }

    private MethodNode getMethod(String name, String signature, ClassNode classNode)
    {
        Iterator<MethodNode> it = classNode.methods.iterator();
        while (it.hasNext())
        {
            MethodNode method = it.next();
            if (method.name.equals(name) && method.desc.equals(signature))
            {
                return method;
            }
        }
        return null;
    }

}