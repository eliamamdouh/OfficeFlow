const axios = require('axios');
const express = require('express');
const { StatusCodes } = require ("http-status-codes");

// const getWorkingDays = async (req, res) => {
// const options = {
//   method: 'GET',
//   url: 'https://working-days.p.rapidapi.com/1.3/add_working_days',
//   params: {
//     country_code: 'CA',
//     start_date: '2024-08-05',
//     increment: '5',
//     include_start: 'true'
//   },
//   headers: {
//     'x-rapidapi-key': '4e59d66a2fmshaac2ce8796580a7p105e0djsnc22412dd0c4c',
//     'x-rapidapi-host': 'working-days.p.rapidapi.com'
//   }
// };

// try {
// 	const response = await axios.request(options);
// 	// console.log(response.data.working_days);
//   let workingDays = response.data.working_days;

  
//   delete workingDays.total;
//   delete workingDays.saturdays;
//   delete workingDays.sundays;
//   delete workingDays.work_hours;
//   delete workingDays.wages;

// console.log(workingDays);

//   res.send(workingDays);

//   // res.status(200).json({ message: 'Working days retrieved successfully' });
//   res.status(StatusCodes.OK).json({ message: 'Working days retrieved successfully' });
// } catch (error) {
// 	console.error(error);
// }
// };
// getWorkingDays();
// module.exports = { getWorkingDays };

const workingDays = async (req, res) => {
  const currentDate = new Date();
  const currentDay = currentDate.getDate();
  const currentMonth = currentDate.getMonth() + 1; // Months are zero-based
  const currentYear = currentDate.getFullYear();

  const startDate = `${currentYear}-${currentMonth
    .toString()
    .padStart(2, "0")}-${currentDay.toString().padStart(2, "0")}`;

  let officeIncrement, homeIncrement;

  if (currentDay <= 15) {
    // First half of the month
    officeIncrement = 3;
    homeIncrement = 2;
  } else {
    // Second half of the month
    officeIncrement = 2;
    homeIncrement = 3;
  }

  const officeDays = ["monday", "tuesday", "wednesday"];
  const homeDays = ["thursday", "friday"];

  const officeOptions = {
    method: "GET",
    url: "https://working-days.p.rapidapi.com/1.3/add_working_days",
    params: {
      country_code: "CA",
      start_date: startDate,
      increment: officeIncrement,
      include_start: "true",
    },
    headers: {
      "x-rapidapi-key": "4e59d66a2fmshaac2ce8796580a7p105e0djsnc22412dd0c4c",
      "x-rapidapi-host": "working-days.p.rapidapi.com",
    },
  };

  const homeOptions = {
    method: "GET",
    url: "https://working-days.p.rapidapi.com/1.3/add_working_days",
    params: {
      country_code: "CA",
      start_date: startDate,
      increment: homeIncrement,
      include_start: "true",
    },
    headers: {
      "x-rapidapi-key": "4e59d66a2fmshaac2ce8796580a7p105e0djsnc22412dd0c4c",
      "x-rapidapi-host": "working-days.p.rapidapi.com",
    },
  };

  try {
    const [officeResponse, homeResponse] = await Promise.all([
      axios.request(officeOptions),
      axios.request(homeOptions),
    ]);

    const filterDays = (data, days) => {
      const filteredDays = {};
      for (const [key, value] of Object.entries(data.days)) {
        if (days.includes(key.toLowerCase())) {
          filteredDays[key] = value;
        }
      }
      return { ...data, days: filteredDays };
    };

    const filteredOfficeDays = filterDays(officeResponse.data, officeDays);
    const filteredHomeDays = filterDays(homeResponse.data, homeDays);

    // Print the working schedules to the terminal
    console.log("Work from Office:", filteredOfficeDays);
    console.log("Work from Home:", filteredHomeDays);

    res.json({
      workFromOffice: filteredOfficeDays,
      workFromHome: filteredHomeDays,
    });
    res.status(StatusCodes.OK).json({ message: 'Working days retrieved successfully' });
  } catch (error) {
    console.error(error);
    // res.status(500).send("An error occurred while fetching working days.");
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).send("An error occurred while fetching working days.");
  }
};

// workingDays();

module.exports = { workingDays };
