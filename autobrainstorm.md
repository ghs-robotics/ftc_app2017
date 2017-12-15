#start: TOP
#read jewels
#hit jewel with poles
#then
#	rotate slightly, look at picture
#	or look at picture while going past later
#go toward crypto-box
#rotate to correct orientation
#use left/right long range IR to tell us how far to strafe
#use front sensor array to get correct distance from crypto box

#TODO:
#figure out where/how to align using IR/other
#figure out last step how it works

#figure out start: BOTTOM
#figure out difference for blue/red


start: TOP
read jewels
read Vumark
hit jewel with poles
then
	get off the balancing stone (using gyro)
	(rotate if blue)
	get to the right distance from the side walls using left/right long range IR
	ram into the cryptobox
	back up
	place glyph
	ram into the cryptobox
	back up

start: BOTTOM
read jewesl
read Vumark
hit jewel with poles
then
	get off the balancing stone (using gyro)
	maintain the right distance from the side walls using left/right long range IR
	rotate using gyro (depending on color)
	ram into the cryptobox
	back up
	place glyph
	ram into the cyrptobox
	back up

TODO:
figure out where/how to align using IR
get off the cryptobox using the gyro
how to back up accurately