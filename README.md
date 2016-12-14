# BoundingBoxOutlineReloaded

BoundingBoxOutlineReloaded is a mod for Minecraft Forge 1.11 and Vanilla 1.11

# Why did I make it?

I loved 4poc's BBOutline mod but the only version I could get to work consistently was for Minecraft 1.6.4. This is fine if you want Nether Fortress bounding boxes but if you need witch huts the new block types can cause Minecraft 1.6.4 to crash horribly; and don't get me started on item frames crashing Minecraft 1.6.4!

In addition to this not working with newer worlds, the way it bounds villages lacks the finesse of KaboPC's VillageMarker mod, and any new structures introduced in Minecraft like Ocean Monuments are missing entirely.

# What it does

This mod highlights in a variety of colours and styles the different structures & features of the game:-
- Nether Fortresses; red boxes bound each individual area where Blaze, Wither Skeletons & normal Skeletons will spawn. Time for a beacon methinks!
- Witch Huts; blue boxes reveal everywhere only witches spawn. Witch farm anyone?
- Desert Temples; orange boxes envelop the pyramid and towers. Go grab some loot but beware TNT boobie traps!
- Jungle Temples; green boxes surround the temple. Indianna Jones would've love these!
- Ocean Monuments; cyan boxes indicate where guardians spawn. New sea lantern and prismarine block types FTW.
- End Cities; magenta boxes show the rooms, corridors and air ships in the End. Time to fall with style!
- Strongholds; yellow boxes show each room in the stronghold. Does anyone make anything with silverfish spawners?
- Woodland Mansions; brown boxes show each room in the woodland mansion. Here's Johnny!
- Mine Shafts; light gray boxes illustrate each of the mine shafts. Cobwebs... grrr!
- Villages; multicoloured spheres encircle  the village, with boxes marking if and where iron golems will spawn. You should see the iron titan... CRAZY!
- Slime chunks; bright green boxes highlight where slimes will spawn, with a dynamic box that rises to where the players feet are to help find them from the surface. Bouncy... bouncy...
- World Spawn & spawn chunks; red boxes outline the world spawn and the spawn chunks (active & lazy).

# How it works

As chunks are loaded the game provides metadata about all the different structures & features in those chunks.  The mod interprets this meta data, caches the results, and renders the bounding boxes to the screen.  In an SMP environment this data is not present on the clients so the mod needs to run on the server where the processing happens and then the relevant metadata is sent to the clients for them to render.

# Installing (Forge Version)

Make sure you have Forge 1.11 installed then drop the jar file into the mods/1.11 folder. Remember this will need to be installed on client and server in an SMP scenario (unless you use local dat files - see below)

# Installing (Vanilla)

Copy the contents of the Vanilla archive into the JAR file as per usual.  In a SMP scenario you'll need to use local dat files - see below.

# Configuring

The keyboard shortcut can be configured in the standard Controls screen.

The following options are available for configuration

Option | Client/Server | Description | Cfg File Key | Cfg File Values | Default
--- | --- | --- | --- | --- | ---
Nether Fortresses | Both | Process/Render Nether Fortresses | drawNetherFortresses | true/false | true
Witch Huts | Both | Process/Render Witch Huts | drawWitchHuts | true/false | true
Desert Temples | Both | Process/Render Desert Temples | drawDesertTemples | true/false | true
Jungle Temples | Both | Process/Render Jungle Temples | drawJungleTemples | true/false | true
Ocean Monuments | Both | Process/Render Ocean Monuments | drawOceanMonuments | true/false | true
End Cities | Both | Process/Render End Cities | drawEndCities | true/false | true
Strongholds | Both | Process/Render Strongholds | drawStrongholds | true/false | false
Woodland Mansions | Both | Process/Render Mansions | drawMansions | true/false | true
Mine Shafts | Both | Process/Render Mine Shafts | drawMineShafts | true/false | false
Villages | Both | Process/Render Villages | drawVillages | true/false | true
Village spheres | Client | Render Villages as spheres instead of cuboids | renderVillageAsSphere | true/false | true
Village Iron Golem Spawn Area | Client | Render Iron Golem Spawn Area within valid Villages | drawIronGolemSpawnArea | true/false | true
Village doors | Client | Render lines between village centre and doors | drawVillageDoors | true/false | false
Slime Chunks | Client | Process/Render Slime Chunks | drawSlimeChunks | true/false | true
Slime Chunks Maximum Y | Client | Maximum Y value of the dynamic slime chunk boxes | slimeChunkMaxY | -1-255 (see below) | -1
World Spawn | Client | Process/Render World Spawn & Spawn Active Chunks | drawWorldSpawn | true/false | true
World Spawn Maximum Y | Client | Maximum Y value of the world spawn & spawn chunk boxes | worldSpawnMaxY | -1-255 (see below) | -1
Lazy Spawn Chunks | Client | Process/Render Lazy Spawn Chunks | drawLazySpawnChunks | true/false | false
Fill | Client | Fill the bounding boxes (except village ones) | fill | true/false | true
Always Visible | Client | Bounding boxes are visible through blocks - kinda messes with perspective tho! | alwaysVisible | true/false | false
Keep Cache Between Sessions | Client | Bounding box caches are not cleared when disconnecting from single or multiplayer worlds. | keepCacheBetweenSessions | true/false | false

The Maximum Y value configuration options have some special values, these are:-
* -1 = the Y value of the players feet when the bounding boxes were activated.
* 0 = the current Y value of the players feet.

Open the config/BBOutlineReloaded.cfg file with your text editor of choice and change the settings.  Simples!  Minecraft (including servers) will need to be restarted for the settings to take effect.

# Using

Press B, sit back and enjoy the goodness flowing onto your screen.
Press O to switch the "Display Outer Boxes Only" mode on and off, this will allow you to see the full boundary of Nether Fortresses, End Cities, Strongholds and Mineshafts

# Using with vanilla servers

There are two options when you want bounding boxes to show whilst accessing vanilla servers:-

1. Keep cache - With the "Keep Cache Between Sessions" config setting enabled, Open a copy of the world in single player and move around to capture all the structures you want in the cache. Once you are happy with the structures you have cached, quit the single player game and connect to the server without closing Minecraft.  You will see all the structures from the cache.
2. Load dat files - Copy the dat files listed below into config/BBOutlineReloaded/{host}/{port} and these will be loaded when you connect to the vanilla server.  {host} is the name or ip you use to connect to the server; {port} is the port you specify when connecting.  The mod will load any/all of following files:-
  - level.dat; include this for world spawn, spawn and slime chunks to be rendered.
  - Fortress.dat; include this for Nether Fortresses to be rendered.
  - EndCities.dat; include this for EndCities to be rendered.
  - Mineshaft.dat; include this for Mineshafts to be rendered.
  - Monument.dat; include this for Ocean Monuments to be rendered.
  - Stronghold.dat; include this for Strongholds to be rendered.
  - Mansion.dat; include this for Mansions to be rendered.
  - Temple.dat; include this for Desert & Jungle Temples and witch huts to be rendered.

  It is also possible to include the villages.dat, villages_end.dat & villages_nether.dat files and it will render villages however these files only contain the villages that were loaded when the files were copied and will not handle any changes that occur with villages such as when doors are added/removed or villager population changes.

# Links
- Forge - [1.11](http://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.11.html)
- 4poc's BBOutline mod - [Forum](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1286555-bounding-box-outline) | [Source](http://www.github.com/4poc/bboutline)
- KaboPC's VillageMarker mod - [Forum](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1288327-village-marker-mod)
