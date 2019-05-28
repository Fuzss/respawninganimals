package com.fuzs.particlefixes.asm;

import com.fuzs.particlefixes.ParticleFixes;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class ClassTransformer implements IClassTransformer {

    private static final String[] classesBeingTransformed =
            {
                    "net.minecraft.world.ServerWorldEventHandler"
            };

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        boolean isObfuscated = !name.equals(transformedName);
        int index = Arrays.asList(classesBeingTransformed).indexOf(transformedName);
        return index != -1 ? transform(index, basicClass, isObfuscated) : basicClass;
    }

    private static byte[] transform(int index, byte[] basicClass, boolean isObfuscated) {

        ParticleFixes.LOGGER.info("Attempting to transform " + classesBeingTransformed[index]);

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            switch (index) {
                case 0:
                    transformServerWorldEventHandler(classNode, isObfuscated);
                    break;
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return basicClass;

    }

    private static void transformServerWorldEventHandler(ClassNode serverWorldEventHandlerClass, boolean isObfuscated) {

        final String SERVERWORLDEVENTHANDLER_SPAWNPARTICLE = isObfuscated ? "a" : "spawnParticle";
        final String SERVERWORLDEVENTHANDLER_SPAWNPARTICLE_DESCRIPTOR = isObfuscated ? "(IZDDDDDD[I)V" : "(IZDDDDDD[I)V";
        boolean flag = false;

        for (MethodNode method : serverWorldEventHandlerClass.methods) {

            if (method.name.equals(SERVERWORLDEVENTHANDLER_SPAWNPARTICLE) && method.desc.equals(SERVERWORLDEVENTHANDLER_SPAWNPARTICLE_DESCRIPTOR)) {

                InsnList newInstructions = new InsnList();

                newInstructions.add(new VarInsnNode(ALOAD, 0));
                newInstructions.add(new FieldInsnNode(GETFIELD, isObfuscated ? "op" : "net/minecraft/world/ServerWorldEventHandler", isObfuscated ? "b" : "world", isObfuscated ? "Loo;" : "Lnet/minecraft/world/WorldServer;"));
                newInstructions.add(new VarInsnNode(ILOAD, 1));
                newInstructions.add(new VarInsnNode(ILOAD, 2));
                newInstructions.add(new VarInsnNode(DLOAD, 3));
                newInstructions.add(new VarInsnNode(DLOAD, 5));
                newInstructions.add(new VarInsnNode(DLOAD, 7));
                newInstructions.add(new VarInsnNode(DLOAD, 9));
                newInstructions.add(new VarInsnNode(DLOAD, 11));
                newInstructions.add(new VarInsnNode(DLOAD, 13));
                newInstructions.add(new VarInsnNode(ALOAD, 15));
                newInstructions.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "spawnServerParticle", isObfuscated ? "(Loo;IZDDDDDD[I)V" : "(Lnet/minecraft/world/WorldServer;IZDDDDDD[I)V", false));

                method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
                flag = true;

            }
        }

        ParticleFixes.LOGGER.info(flag ? "Transformation successful" : "Transformation failed");

    }

}
