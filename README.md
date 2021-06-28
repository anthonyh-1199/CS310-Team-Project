# Employee-Time-Clock

## Description

This project simulates a business's time clock system that would be used to record the clocking times of employees and make calculations based off of said data.

This project was done for CS310 - Software Engineering I and was completed with a team of three other programmers. The purpose of the project was to gain experience working with object-orientated programming design patterns and to expose team members to agile methodoligies (specifically Scrum).

## How It Works

This project achieves this by breaking down each element of the system into a Java class:
- *Badge* represents the employees' unique IDs
- *Department* represents the different departments in the work location
- *Shift* holds the start and end times for different work hours as well as lunch breaks
- *Punch* represents individual punch-ins and punch-outs for shifts
- *Employee* holds data about individual employees, including their names, departments, shifts, and badges

The system connects to the SQL database that holds all of this information and parses it into these classes. 

This data is then used to calculate several metrics, including:
- A list of punches made during a specific time period
- An employee's tardiness/absenteeism during a specific time period
- The amount of regular and overtime hours an employee worked for a given pay period
- A summary of all active employees in a specific department

## PDF Reports

This project also utilizes the open-source Java tool JasperReports to export these summaries into concise, easy-to-read PDF reports.

Examples of these reports are shown below (all persons listed are fictional):

![alt text](https://i.imgur.com/fg68M8O.png)

![alt text](https://i.imgur.com/j1dg7Lw.png)

![alt text](https://i.imgur.com/UhgE2qk.png)
