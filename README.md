# Bachelor's degree final project - IoT Solution For Flight Monitoring

### Description ###
The aim of this solution is real-time monitoring of air traffic and proposes a way of recording, transmitting and displaying transport data, integrated into an application Android application called Air Spotter.


### Motivation ###
The development of this app was motivated by the growing need to monitor increasingly crowded air traffic. Air Spotter caters to aviation enthusiasts, aviation professionals and anyone interested in aircraft by giving them a comprehensive view of flights in real time.

### Key features ###
* Monitor air transport quality: This functionality facilitates the transmission of data and statistics on humidity, temperature and vibration recorded in a container transported in an aircraft. This information is crucial for ensuring the integrity of the goods being transported, especially for environmentally sensitive commercial shipments.
* Augmented reality for aircraft visualisation: Air Spotter gives users the ability to visualise the aircraft around them within a 30-kilometre radius, providing detailed information about each aircraft.
* Delay prediction: This essential functionality in a busy and constantly moving world analyses the performance of various air transport components to identify the root causes of delays and develop preventative solutions for the future.
* Gamification elements: I've added interactive elements and engaging designs to enhance the user experience and make the app more engaging and fun.

### Technologies ###
* The main application of the solution (the present Android app) was build using Java as main programming language.
* The model used for the prediciton delay functionality was build using Python and integrated in the app using Chaquopy.
* The augmented reality functionality was build using Sceneform SDK and ARCore.
* Finally, the functionality for shipment quality was build using a Raspberry Pi 4b, a DHT22 sensor for temperature and humidity and a SW-420 sensor for vibration, and was implemented using Python as programming language. To transmit the data in the Android app, I've build a Flask API.


### All flight information is obtained through the use of Opensky Network REST APIs. ### 
