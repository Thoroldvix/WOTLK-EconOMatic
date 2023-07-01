# WOTLK EconOMatic

EconOMatic is an API service designed to give easy access to economy related data for WoW Classic WOTLK.

## Table of contents

* [Project Description](#project-description)
* [Features](#features)
* [Technologies](#technologies)
* [Installation](#installation)
* [Usage](#usage)
* [API Reference](#api-reference)
* [Configuration](#configuration)
* [Contributing](#contributing)
* [License](#license)

## Project Description

EconOMatic combines information about the game's economy into one easy-to-use API. It's great for players interested in
the market or for professional gold traders. The API can also be plugged into your own projects to build custom tools
and applications.
This API makes it easy for players to understand the game's economy, make smart trading decisions, and plan their gold
farming strategies. It gives all of this information in a simple and clear way, making it accessible for everyone.

## Features

EconOMatic API has a bunch of useful features. Let's take a look at some of them:

1. ### Server Information Retrieval
   You can get a list of available servers for WoW Classic WOTLK, including their names, regions, and other important
   details.

2. ### Item Information Retrieval
   You can also get information about the items in the game, like their IDs, names, descriptions, and rarity.

3. ### Population Retrieval for Servers
   Get population stats for each server, including the player count and faction distribution. This can help you choose
   the
   right server and get a sense of the game's demographics. Population information is provided
   by [WarcraftTavern](https://www.warcrafttavern.com/population/wotlk).

4. ### Gold price Retrieval for Servers
   Stay updated with the current gold prices for each server. This feature tells you when gold prices go up or down,
   which can help you buy low and sell high or decide which server is better to sell gold on. Gold prices are
   fetched from [G2G](https://www.g2g.com).

5. ### Auction House Information Retrieval
   Get detailed auction house information for a specific server. See how much different items are selling for and how
   much supply there is. This can help you spot good deals and stay ahead of the market. Auction House information is
   provided
   by [NexusHub](https://nexushub.co).

6. ### Server Recommendation Retrieval
   Get recommendations on the best servers to farm on based on the items you're planning to farm. The recommendations
   take into account population, item prices, and gold prices.

7. ### Item Deals Retrieval
   This feature provides a list of the best deals for items on a specific server. It helps you identify items that you
   can buy at a low price and sell at a higher price. The discount percentage is calculated based on the difference
   between market value and minimum buyout price.

8. ### Data Filtering
   You can customize and filter the data to get exactly the information you want. Look for specific types of items,
   price ranges, server regions, and more.

9. ### Integration Flexibility
   You can integrate the API into your own projects and applications. This allows you to take full advantage of all the
   features and build custom tools that suit your needs.

Using the EconOMatic API, you can learn a lot about the WoW Classic WOTLK economy and use that knowledge to make smarter
trading decisions and farm gold more effectively.

## Technologies

* ### Java 17
* ### Spring Boot
* ### Lombok
* ### Postgresql
* ### Hibernate
* ### Liquibase

## Installation

The EconOMatic application can be set up in multiple ways. Here are the two most common ways to run the service:

### Option 1: Local Postgres Database

Before you begin, ensure you have a running local PostgreSQL database. This could be within a Docker container or
installed on your machine.
Next, set up the environment variables. Defaults are provided, but you can adjust according to your needs:

* `DB_URL` - Link to your database. Default: `jdbc:postgresql://localhost:5432/economatic`
* `DB_USERNAME` - Username for your database. Default: `postgres`
* `DB_PASS` - Password for your database. Default: `pass`
* `PORT` - Port to run application on. Default: `8080`

Navigate to the code directory and run:

```shell
DB_URL=your_value DB_USERNAME=your_value DB_PASS=your_value PORT=your_value ./gradlew bootRun
```

Replace your_value with your values.

### Option 2: Docker Compose

Alternatively, utilize the Docker compose file provided. In the code directory, use:

```shell
docker-compose up -d
```

When you want to stop the application:

```shell
docker-compose down
```

Docker helps in managing the application without the need for manual setup.

## Usage

Before you start, ensure the EconOMatic application is running on your machine.
You have two straightforward options to interact with the EconOMatic API:

1. **Using Postman**: This offers a user-friendly visual interface. A ready-to-use Postman collection is available,
   which covers all the endpoints. This way, you can immediately start interacting and experimenting with the API after
   setting it up. Follow these steps:
    * Download the Postman collection using the following
      link: [EconOMatic Postman Collection](https://api.postman.com/collections/27069629-4238bc33-f1cb-473e-a1ae-78ac80161e3a?access_key=PMAT-01H49D33FCR65VXZAZVXRBVSFV).
    * Import this collection into your Postman application. If you're uncertain about this step, Postman provides a
      comprehensive guide
      available [here](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/).
2. **Sending requests directly from your browser**: Another method is to send the requests directly from your browser.
   Enter the URL with the specific endpoint you're interested in. For a complete list of available endpoints and their
   usage, refer to the [API Reference](#api-reference) section.
   Either way, you will find EconOMatic API a helpful tool in understanding WoW Classic WOTLK's economy, planning your
   gold
   farming strategies, and making smart trading decisions.

## Api Reference

Comprehensive documentation for each API endpoint is accessible through the Swagger UI, located
at http://localhost:8080/swagger-ui/index.html. If app is running on a port other than the default 8080, make
sure to update the URL accordingly.

Before attempting to access the Swagger UI, please ensure that the application is running as expected on your 
machine

## Configuration

Below are additional environmental variables that can be configured to adjust the application’s functionality:

* `GOLD_PRICE_DEFAULT_WEIGHT` Default weight for gold price recommendation calculations. Default: `1`
* `ITEM_PRICE_DEFAULT_WEIGHT` Default weight for item price recommendation calculations. Default: `1`
* `POPULATION_DEFAULT_WEIGHT`: Default weight for population recommendation calculations. Default: `0.1`
* `MIN_ALLOWED_POPULATION` Minimum allowed population when calculating recommendation scores for servers.
  Default: `1000`
* `UPDATE_ON_STARTUP` Defines whether to update gold price, item price, and population data on startup. Useful to avoid
  redundant fetching of data when the application is restarted frequently. Default: `true`
* `POPULATION_UPDATE_RATE`: Frequency to update population data in days. Default: `1`
* `ITEM_PRICE_UPDATE_RATE`: Frequency to update item price data in hours. Default `3`
* `GOLD_PRICE_UPDATE_RATE` Frequency to update gold price data in minutes. Default: `60`

When setting up the application, ensure these are considered and adjusted according to your use case. To keep the
defaults, no action is needed.

## Contributing

Contributions to this project are most welcome. If you have an idea for a feature, want to suggest an enhancement, or
need to report a bug, feel free to open an issue or submit a pull request. Please follow these steps for contributing:

1. Fork the project repository.

2. Clone the forked repository to your local machine.

3. Make your changes in your local repository.

4. If you're suggesting a feature or an enhancement, write a clear and detailed proposal for how it should work.

5. After making your changes, run the tests to ensure everything works as expected.

6. Commit your changes with a clear and concise commit message.

7. Push your changes to your forked repository.

8. Submit a pull request, detailing the changes you made or the features you propose, and explain why they should be
   included.

All pull requests and feature suggestions will go through a review and feedback process. We aim to merge pull requests
and implement useful features that align with the project's objectives as soon as possible.

## License

This project is licensed under the MIT License. This means you are free to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the software. However, you must include the following notice in all copies
or substantial portions of the software:

"The software is provided "as is", without warranty of any kind, express or implied, including but not limited to the
warranties of merchantability, fitness for a particular purpose and noninfringement. In no event shall the authors or
copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or
otherwise, arising from, out of or in connection with the software or the use or other dealings in the software."



