 define exploresensor sensor 
(
    size [0.01 0.05 0.01]
    range [0 10]
    fov 1
    samples 1
    color_rgba [0 1 0 0.2]
)


define obstacle_ranger ranger 
(
   exploresensor ( pose [4 0 0.1 0] )
   exploresensor ( pose [-4 0 0.1 180])
)

# definition of the robot
define obstacle_base position (

   # actual size of robot
   size [5 1 2]
   
   # other parameters
   origin [0 0 0 0]
   gui_nose 1
   obstacle_return 1
   ranger_return 1
   blob_return 0
   fiducial_return 0
   localization "gps"
   localization_origin [0 0 0 0]

   # choose a differential drive for the robot
   drive "diff"     
)

define obstacle_with_sensors obstacle_base 
(
   robot_ranger( pose [0 0 0 0] )
)

define obstaclebot obstacle_with_sensors
(
   # robot body
   block
   (
      points 4
      point[0] [0 0]
      point[1] [5 0]
      point[2] [5 1]
      point[3] [0 1]
      z [0 2]
      color "green"
   )
)
