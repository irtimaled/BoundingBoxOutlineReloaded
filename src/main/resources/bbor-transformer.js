function initializeCoreMod() {
	Opcodes = Java.type("org.objectweb.asm.Opcodes");
    ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    InsnList = Java.type("org.objectweb.asm.tree.InsnList");
    LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
    VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
	MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

    processPacket = ASMAPI.mapMethod("func_148833_a");

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