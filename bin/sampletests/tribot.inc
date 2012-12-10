 define exploresensor sensor 
(
    size [0.01 0.05 0.01]
    range [0 10]
    fov 1
    samples 1
    color_rgba [0 1 0 0.2]
)


define robot_ranger ranger 
(
   exploresensor ( pose [0.1255 -0.05 0.1 0] )
   exploresensor ( pose [0.1255 0.05 0.1 0] )
   exploresensor ( pose [0.05 0.0643 0.1 90])
   exploresensor ( pose [-0.05 0.0643 0.1 90])
   exploresensor ( pose [-0.1255 0.05 0.1 180])
   exploresensor ( pose [-0.1255 -0.05 0.1 180])
   exploresensor ( pose [-0.05 -0.0643 0.1 270])
   exploresensor ( pose [0.05 -0.0643 0.1 270])
)

# definition of the robot
define robot_base position (

   # actual size of robot
   size [0.251 0.1286 0.12]
   
   # other parameters
   origin [0 0 0 0]
   gui_nose 1
   obstacle_return 1
   ranger_return 0.5
   blob_return 0
   fiducial_return 0
   localization "gps"
   localization_origin [0 0 0 0]

   # choose a differential drive for the robot
   drive "diff"   

   
)

define robot_with_sensors robot_base 
(
   robot_ranger( pose [0 0 0 0] )
)

define tribot robot_with_sensors
(
   # robot body
   block
   (
      points 4
      point[0] [0 0]
      point[1] [0.2 0]
      point[2] [0.2 0.1]
      point[3] [0 0.1]
      z [0 0.12]
      color "red"
   )

   # left wheel
   block
   (
      points 4
      point[0] [0.08 0.101]
      point[1] [0.18 0.101]
      point[2] [0.18 0.126]
      point[3] [0.08 0.126]
      z [0 0.1]
      color "red"
   )

   # right wheel
   block
   (
      points 4
      point[0] [0.08 -0.026]
      point[1] [0.18 -0.026]
      point[2] [0.18 -0.001]
      point[3] [0.08 -0.001]
      z [0 0.1]
      color "red"
   )

   # back wheel
   block
   (
      points 4
      point[0] [-0.051 0.0375]
      point[1] [-0.001 0.0375]
      point[2] [-0.001 0.0625]
      point[3] [-0.051 0.0625]
      z [0 0.1]
      color "red"
   )
)
