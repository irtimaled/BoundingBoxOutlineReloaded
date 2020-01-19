# BoundingBoxOutlineReloaded

BoundingBoxOutlineReloaded is a mod for Minecraft Vanilla, Forge, LiteLoader, Rift, and Fabric

## What it does

This mod highlights in a variety of colours and styles the different structures & features of the game:-
- Nether Fortresses; red boxes bound each individual area where Blaze, Wither Skeletons & normal Skeletons will spawn. Time for a beacon methinks!
- Witch Huts; blue boxes reveal everywhere only witches spawn. Witch farm anyone?
- Desert Temples; orange boxes envelop the pyramid and towers. Go grab some loot but beware TNT boobie traps!
- Jungle Temples; dark green boxes surround the temple. Indianna Jones would've love these!
- Ocean Monuments; cyan boxes indicate where guardians spawn. Sea lantern and prismarine block types FTW.
- End Cities; magenta boxes show the rooms, corridors and air ships in the End. Time to fall with style!
- Strongholds; yellow boxes show each room in the stronghold. Does anyone make anything with silverfish spawners?
- Woodland Mansions; brown boxes show each room in the woodland mansion. Here's Johnny!
- Mine Shafts; light gray boxes illustrate each of the mine shafts. Cobwebs... grrr!
- Villages (*); gray boxes designate each building in the generated village. Check out the blacksmith loot chest!
- Slime chunks; dark green boxes highlight where slimes will spawn, with a dynamic box that rises to where the players feet are to help find them from the surface. Bouncy... bouncy...
- World Spawn & spawn chunks; red boxes outline the world spawn and the spawn chunks (active & lazy).
- Igloos (*); white boxes show where igloos are. Maybe you can convert the zombie villager back?
- Shipwrecks (*); cyan boxes are like a lighthouse those wary sailors wish they'd had. Time to find some buried treasure? 
- Ocean ruins (*); cyan boxes show the different ocean ruin structures. Watch out for those pesky drowns tho!
- Buried treasure (*); cyan boxes highlight where the heart of the sea can be found. Conduit anyone?
- Mob Spawners; bright green boxes show where mob spawners are, where mobs will be spawned, and red/orange/green lines help the player see if a spawner is nearby and activated.
- Pillager Outposts; dark gray boxes outline where crossbow wielding pillagers will spawn. Beware of bad omens tho!
- Village Spheres (+); multicoloured spheres encircle the village, with boxes marking if and where iron golems will spawn. You should see the iron titan... CRAZY!

(*) Prior to 1.14.4, due to how Minecraft generates these structures they will initially float above where they should be, however upon re-loading the world they should drop down to the correct height. Unfortunately (or fortunately?) buried treasure will always appear at y-90. This works as expected in 1.14.4.
(+) In 1.14 and above the concept of a village based on doors being involved in iron golem spawning has gone away, so this feature goes away too.

## Why did I make it?

I loved 4poc's BBOutline mod but the only version I could get to work consistently was for Minecraft 1.6.4. This is fine if you want Nether Fortress bounding boxes but if you need witch huts the new block types can cause Minecraft 1.6.4 to crash horribly; and don't get me started on item frames crashing Minecraft 1.6.4!

In addition to this not working with newer worlds, the way it bounds villages lacks the finesse of KaboPC's VillageMarker mod, and any new structures introduced in Minecraft are missing entirely.

## How it works

As chunks are loaded the game provides metadata about all the different structures & features in those chunks. The mod interprets this meta data, caches the results, and renders the bounding boxes to the screen. In an SMP environment this data is not present on the clients so the mod needs to run on the server where the processing happens and then the relevant metadata is sent to the clients for them to render.

## Installing (Forge/LiteLoader/Rift/Fabric)

Make sure you have the relevant mod loader installed then drop the mod file into the appropriate mods folder. This approach can be used for client and server deployments where needed.

## Installing (Vanilla)

Double-click the jar file and a client profile for the relevant version of Minecraft will be created/updated in the launcher.

## Running a server (Vanilla)

To start a vanilla server with the mod loaded, copy the relevant jar file to a folder, and run:-
```
java -jar BBOutlineReloaded-{version}.jar --server
```

Running this will download the necessary pre-requisities and start the server.

## Configuring

The keyboard shortcuts can be configured in the standard Controls screen.

Most of the options are available for configuration on the client through a configuration Gui. On Vanilla, Rift & Fabric there is a BBOR button on the options screen, alternatively press and hold B key when in game.

In the table below are a couple of old config options that can only be edited by opening the config/BBOutlineReloaded.cfg file with a text editor. The Minecraft Client will need to be restarted for the settings changed this way to take effect.

Option | Description | Cfg File Key | Cfg File Values | Default
--- | --- | --- | --- | ---
Always Visible | Bounding boxes are visible through blocks - kinda messes with perspective tho! | alwaysVisible | true/false | false
Keep Cache Between Sessions | Bounding box caches are not cleared when disconnecting from single or multiplayer worlds. | keepCacheBetweenSessions | true/false | false

## Using

- Press B, sit back and enjoy the goodness flowing onto your screen.
- Press and hold B+G to open the configuration Gui.
- Press B+O to switch the "Display Outer Boxes Only" mode on and off, this will allow you to see the full boundary of Nether Fortresses, End Cities, Strongholds and Mineshafts

## Bounding boxes when connected to servers

There are a couple of options when you want bounding boxes to show whilst accessing servers:-

- Use a modded server - Ensure the server is running with this mod loaded (as described above), and connect to the server with a client with this mod loaded. 
- Keep cache - With the "Keep Cache Between Sessions" config setting enabled, Open a copy of the world in single player and move around to capture all the structures you want in the cache. Once you are happy with the structures you have cached, quit the single player game and connect to the server without closing Minecraft. You will see all the structures from the cache.

## Links
- Forge - [Download](https://files.minecraftforge.net/)
- 4poc's BBOutline mod - [Forum](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1286555-bounding-box-outline) | [Source](http://www.github.com/4poc/bboutline)
- KaboPC's VillageMarker mod - [Forum](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1288327-village-marker-mod)
