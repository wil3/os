os
==

Online Signature (OS)

Determines the likelihood your computer is able to access the internet at any given time of day. The service records activity given the specified sample. Samples may be taken at a maximum of 60 times per hour and a minimum of once an hour. The visualizer plots the data during a 24 hour period. The event of the internet being research at a given time is a Bernoulli trial. The plot displays the results for the 24 Bernoilli trials.

Building
=======

mvn clean install


Running Service
===============

Place in os.conf in /etc/init/

sudo start os

sudo stop os