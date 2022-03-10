<img src="title.png" alt="Entity Texture Features Title" width="650"/>

___
### [[Discord]](https://discord.gg/rURmwrzUcz) - [[CurseForge]](https://www.curseforge.com/minecraft/mc-mods/entity-texture-features-fabric) - [[Modrinth]](https://modrinth.com/mod/entitytexturefeatures) - [[~~Compatability List~~]]() - [[Report Issues]](https://github.com/Traben-0/EmissiveMod/issues) - [[Donate]](https://www.paypal.com/donate/?business=PLB862YH8UXML&no_recurring=0&currency_code=AUD)
___
# 🎨  Player skin features Guide
<img src="skins1.png" alt="img" width="450"/>
<img src="skins2.png" alt="img" width="450"/>
<img src="skins3.png" alt="img" width="450"/>
<img src="jacket.png" alt="img" width="450"/>

most of these examples are downloadable [[HERE]](https://github.com/Traben-0/EmissiveMod/raw/master/Skin_Feature_Examples.ZIP)
- Player skins can use emissive, blinking, enchanted, and transparency texture features and more...
- 100% optional, controlled by the skin file you upload to Mojang, and options to prevent abuse of features in PVP

# How can you do this?
## The Example Skin File
###### *Not as scary as it looks all will be explained below*
  <img src="mod_data_example_details.png" alt="img" width="650"/>

[*Downloadable example skin*](mod_data_example.png)  
![downloadable](mod_data_example.png)
## Getting started
- First of all to enable the skin features in this mod you **must** put the marker in your skin file,
  this is the red,green,blue,white,black pixels just below the head texture in the example image above.
- This mod will ignore any skin without this so no one else can affect **YOUR** skin
- You will select your marker choices later these will be selected by putting specific
colour pixels in the boxes #1 - #4 in the marker
- The choices Box is the box to the left of the red #1 box in the example
you will fill this in later with specific pixel colours to select options for your skin
- The Color Guide is not required - it is provided in the skin for you to grab the specific
colors required for later choices. *(Note: each color corresponds to a number, this will be used later)* 

- It is **highly** recommended you leave every unused space ion your skin texture blank and transparent,
if you have stray pixels filled they may trigger future features 

- I will promise now. As long as you leave unused parts of your skin file 
blank and transparent no future update will impact your skin unexpectedly.

## Transparency - [V2.3.0+]
- If the marker is present in your skin, you will then be able to use transparency in
the head, body,RLeg, LLeg,RArm, and LArm, body parts (this is disabled in vanilla).
- The total skin can not be less than 40% average transparency to prevent possible PVP abuse
an option may be added to override this for fun in future
- See examples above in the Ghost, Slime, Steve, Chicken, Skeleton, and Among us skins
can be seperately Disabled for enemy team players so as to not be abused in PVP settings
## Emissiveness - [V2.3.0+]
- If the marker is present in your skin, you will then be able to use emissiveness in your skin
- To enable Emissiveness you **must** choose **only one** numbered pixel 
in the marker and give it the Emissive color *(the pink in the marker choices list)  
(for example putting the pink color in #1 in the marker will select all the pixels in the red box to the right side of the skin with a #1 in it, and select it for emissives)*
- After choosing a spot in the marker, any pixels present in the chosen 
square of the same number, to the right of the skin, will be matched against
the rest of the skin, and any matching pixels of the exact same colour & opacity will glow
- See examples above in the Ghost, Robot, and Thanos's gauntlet
## Enchanting - [V2.3.0+]
- If the marker is present in your skin, you will then be able to use enchanting visuals in your skin
- To enable Enchanted visuals you **must** choose **only one** numbered pixel
   in the marker and give it the Enchanted color *(the cyan in the marker choices list)   
(for example putting the cyan color in #2 in the marker will select all the pixels in the green box to the right side of the skin with a #2 in it, and select it for enchanting)*
- After choosing a spot in the marker, any pixels present in the chosen
  square of the same number, to the right of the skin, will be matched against
the rest of the skin, and any matching pixels of the exact same colour & opacity will appear enchanted on your skin
- See examples above in the Alex, robed figure, and Thano's arm skins
## Blinking - [V2.3.0+]
- If the marker is present in your skin, you will then be able to have your skin blink periodically
but you must also choose what style of blinking,
```
- 1 pixel height Eyes      [V2.4.4+]
- 2 pixel height Eyes      [V2.4.4+]
- 3-4 pixel height Eyes    [V2.4.4+]
- Whole face texture blink 
```
#### 1 pixel blinking - [V2.4.4+]
If your skin's eyes are only 1 pixel tall - use this.
- First place a Red #3 pixel in Choice Box pixel #1 to enable 1 pixel blinking
- Then make a copy of the horizontal line of the skin's face where the eyes are, and change the 
eyes to be closed.
- Place this "closed eye copy" in the same place as the pink line below the head in the Example Image.
- Next the mod needs to know where your eyes are, place a numbered Color from the Color Guide in Choice Box #4 
corresponding to the height of the skin's eyes   
*(The head texture is 8 pixels tall with #1 being the top line of the head & #8 being the bottom)*  
*(For Example Default Steve's eyes are at line #5, so you would place the Brown #5 pixel in Choice Box #4)*
- You are now done, the mod will take the Pink line below the head texture 
and replace your skin's eyes with it when it blinks
- In the example skin pack the file "blinkOption3.png" uses the 1 pixel blinking

#### 2 pixel blinking - [V2.4.4+]
If your skin's eyes are only 2 pixels tall - use this.
- First place a Green #4 pixel in Choice Box pixel #1 to enable 2 pixel blinking
- Then make 2 copies of the 2 horizontal lines of the skin's face where the eyes are, and change 1 copy's
  eyes to be closed, and the other's eyes to be half closed.
- Place the "closed eye copy" in the same place as the pink line & the purple line below the head in the Example Image.
- Place the "half closed eye copy" in the same place as the white / purple checkered area below the head in the Example Image.
- Next the mod needs to know where your eyes are, place a numbered Color from the Color Guide in Choice Box #4
  corresponding to the height of the skin's eyes topmost pixel   
  *(The head texture is 8 pixels tall with #1 being the top line of the head & #8 being the bottom)*  
  *(For Example Default Steve's eyes are at line #5, so you would place the Brown #5 pixel in Choice Box #4)*
- You are now done, the mod will take the appropriate eye copy below the head texture
  and replace your skin's eyes with it when it blinks
- In the example skin pack the file "blinkOption4.png" uses the 2 pixel blinking

#### 3-4 pixel blinking - [V2.4.4+]
If your skin's eyes are only 3-4 pixels tall - use this.  
This actually only supports 4 pixels but you can simply copy an additional *non-eye* line of the face if your 
eyes are 3 pixels high and it will work fine :)
- First place a Brown #5 pixel in Choice Box pixel #1 to enable 4 pixel blinking
- Then make 2 copies of the 4 horizontal lines of the skin's face where the eyes are, and change 1 copy's
  eyes to be closed, and the other's eyes to be half closed.
- Place the "closed eye copy" in the same place as the pink & purple & checkerboard area below the head in the Example Image.
- Place the "half closed eye copy" in the same place as the white / green checkered area below the head's 2nd layer in the Example Image.
- Next the mod needs to know where your eyes are, place a numbered Color from the Color Guide in Choice Box #4
  corresponding to the height of the topmost pixel of the face you copied from *(where the eyes were)*   
  *(The head texture is 8 pixels tall with #1 being the top line of the head & #8 being the bottom)*  
  *(For Example Default Steve's eyes are at line #5, so you would place the Brown #5 pixel in Choice Box #4)*
- You are now done, the mod will take the appropriate eye copy below the head textures
  and replace your skin's eyes with it when it blinks
- In the example skin pack the file "blinkOption5.png" uses this 3-4 pixel blinking

#### Whole face texture blinking
The whole face blinking option is the easiest way to do skin blinking but will use more space in the texture
and may limit what features can be used in the future, this is only recommended for eyes larger than 
4 pixels or not 'typical' eyes
- First choose what kind of blinking you want, many skins have large eyes, so to smooth out blinking 
you can have either
- 1 frame of blinking (eye open & eye closed) or 2 frames (eye open & eye half closed & eye closed)
- to select one you must change the colour of Choice Box pixel #1 in the example above
- to Select 1 frame you must use exactly the Pink pixel #1 from the Color Guide, for 2 frames 
use the Light Blue #2 instead
- Once this is done you can now add blinking textures to your skin, make a copy of the front of
your face and put it in the purple box with a #1
make this face have it's eyes completely closed
- Repeat with the extra head layer for your face and put that in the purple box with #3, 
just incase you have popout eyes
- if you have selected a 2 frame blink repeat this process with purple boxes #2 & #4 except this time make the eyes half closed
- In the [example skins download pack](https://github.com/Traben-0/EmissiveMod/raw/master/Skin_Feature_Examples.ZIP) you can see this in the Chicken & Slime Tuxedo

## Jacket / Dress Extensions - [V2.4.0+]
<img src="jacket.png" alt="img" width="400"/>

- This feature provides extension options for how minecraft renders the 'Jacket' layer of your minecraft 
skin *(the second layer that floats above your body)* and allows it to go up to 8 pixels further down
- Typical uses could be for Jackets, Dresses, Villager cloaks and Robes.
- The texturing is extremely straight forward, if enabled this feature will copy the contents of the top of the
Legs floating skin layers and use them to build the extension *(these pixels are marked by a checkerboard pattern in the example image above)*.

To enable this feature **must** choose what ***Style*** of extension you want.  
This is done by putting a pixel of your choice in Choice Box #2, the numbers below correspond to the
colored pixels in the Color Guide
```
1. you will get the examples above where the extension is copied from the leg overlay which will still have the original texture.
2. the extension is still copied from the leg overlay, but it will also delete those pixels from the leg overlay texture itself leaving them clear.
3. same as #1 but with a wider extension
4. same as #2 but with a wider extension
5. same as #1 but will ignore the top of leg texture  [V2.4.4+]
6. same as #2 but will ignore the top of leg texture  [V2.4.4+]
7. same as #3 but will ignore the top of leg texture  [V2.4.4+]
8. same as #4 but will ignore the top of leg texture  [V2.4.4+]
```
Next you **must** choose what ***Length*** of extension you want.  
This is done by putting a pixel of your choice in Choice Box #3, The color of pixel you choose must
be one from the Color Guide.
The number for that color in the Color Guide will be the extension length from 1 to 8 pixels.


# To confusing or not working?
join the [[Discord]](https://discord.gg/rURmwrzUcz) and i'll be happy to help

.  
.  
.  
.  
.  
.  
.  
.  
.  
.  
.
.  
.  
.  
.  
.  
.  
.  
.  
.  
.  
.