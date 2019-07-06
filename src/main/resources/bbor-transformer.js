function initializeCoreMod() {
	Opcodes = Java.type("org.objectweb.asm.Opcodes");
    ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    InsnList = Java.type("org.objectweb.asm.tree.InsnList");
    LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
    VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
	MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

    processPacket = ASMAPI.mapMethod("func_148833_a");
    func_228426_a_ = ASMAPI.mapMethod("func_228426_a_");
    func_228447_c_ = ASMAPI.mapMethod("func_228447_c_");
    func_228446_b_ = ASMAPI.mapMethod("func_228446_b_");

    return {
        "SCommandListPacket": {
            "target": {
              "type": "CLASS",
              "name": "net.minecraft.network.play.server.SCommandListPacket",
            },
            "transformer": function(classNode) {
                var methods = classNode.methods;
                for (var method in methods) {
                    var methodNode = methods[method];
                    if (methodNode.name == processPacket) {
                       inject_SCommandListPacket_processPacket(methodNode.instructions);
                       break;
                    }
                }
                return classNode;
            }
        },
        "SSpawnPositionPacket": {
            "target": {
              "type": "CLASS",
              "name": "net.minecraft.network.play.server.SSpawnPositionPacket",
            },
            "transformer": function(classNode) {
                var methods = classNode.methods;
                for (var method in methods) {
                    var methodNode = methods[method];
                    if (methodNode.name == processPacket) {
                       inject_SSpawnPositionPacket_processPacket(methodNode.instructions);
                       break;
                    }
                }
                return classNode;
            }
        },
        "SChunkDataPacket": {
            "target": {
              "type": "CLASS",
              "name": "net.minecraft.network.play.server.SChunkDataPacket",
            },
            "transformer": function(classNode) {
                var methods = classNode.methods;
                for (var method in methods) {
                    var methodNode = methods[method];
                    if (methodNode.name == processPacket) {
                       inject_SChunkDataPacket_processPacket(methodNode.instructions);
                       break;
                    }
                }
                return classNode;
            }
        },
        "WorldRenderer": {
            "target": {
              "type": "CLASS",
              "name": "net.minecraft.client.renderer.WorldRenderer",
            },
            "transformer": function(classNode) {
                var methods = classNode.methods;
                for (var method in methods) {
                    var methodNode = methods[method];
                    if (methodNode.name == func_228426_a_) {
                       inject_WorldRenderer_func_228426_a_(methodNode.instructions);
                       break;
                    }
                }
                return classNode;
            }
        }
    };
}

function inject_SCommandListPacket_processPacket(instructions) {
    var returnInstruction = getInstructionOrThrow(instructions, Opcodes.RETURN);

    var newInstructions = new InsnList();
    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                           "com/irtimaled/bbor/forge/ForgeClientInterop",
                                           "registerClientCommands",
                                           "(Lnet/minecraft/client/network/play/IClientPlayNetHandler;)V",
                                           false));
    instructions.insertBefore(returnInstruction, newInstructions);
}

function inject_SSpawnPositionPacket_processPacket(instructions) {
    var returnInstruction = getInstructionOrThrow(instructions, Opcodes.RETURN);

    var newInstructions = new InsnList();
    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                           "com/irtimaled/bbor/forge/ForgeClientInterop",
                                           "updateWorldSpawnReceived",
                                           "(Lnet/minecraft/network/play/server/SSpawnPositionPacket;)V",
                                           false));
    instructions.insertBefore(returnInstruction, newInstructions);
}

function inject_SChunkDataPacket_processPacket(instructions) {
    var returnInstruction = getInstructionOrThrow(instructions, Opcodes.RETURN);

    var newInstructions = new InsnList();
    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                           "com/irtimaled/bbor/forge/ForgeClientInterop",
                                           "receivedChunk",
                                           "(Lnet/minecraft/network/play/server/SChunkDataPacket;)V",
                                           false));
    instructions.insertBefore(returnInstruction, newInstructions);
}

function inject_WorldRenderer_func_228426_a_(instructions) {
    var instruction1 = getInstructionOrThrow(instructions, Opcodes.INVOKESPECIAL, func_228447_c_)

    var newInstructions1 = new InsnList();
    newInstructions1.add(new VarInsnNode(Opcodes.FLOAD, 2));
    newInstructions1.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                           "com/irtimaled/bbor/forge/ForgeClientInterop",
                                           "render",
                                           "(F)V",
                                           false));
    instructions.insert(instruction1, newInstructions1);

    var instruction2 = getInstructionOrThrow(instructions, Opcodes.INVOKESPECIAL, func_228446_b_)

    var newInstructions2 = new InsnList();
    newInstructions2.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                           "com/irtimaled/bbor/forge/ForgeClientInterop",
                                           "renderDeferred",
                                           "()V",
                                           false));
    instructions.insert(instruction2, newInstructions2);

}

function getInstructionOrThrow(instructions, opcode, name) {
    var size = instructions.size();
    for (var index = 0; index < size; ++index) {
        var instruction = instructions.get(index);
        if (instruction.getOpcode() == opcode && (!name || instruction.name == name)) {
            return instruction;
        }
    }
    throw "Error: Couldn't find injection point!";
}