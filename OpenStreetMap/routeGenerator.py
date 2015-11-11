#!/usr/bin/python
# -*- coding: utf-8 -*-
"""
Copyright 2015 Ericsson AB
Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
"""
import ast
import multiprocessing
import requests

ROUTES_GENERATOR_HOST = 'http://localhost:'
ROUTES_GENERATOR_PORT = '9998'

headers = {'Content-type': 'application/x-www-form-urlencoded'}
print_lock = multiprocessing.Lock()


def string_to_coordinates(string):
    """
    Translates a string to a bus stop or address. If there is no address or
    bus stop with that name, both address and bus_stop are False and longitude
    and latitude have the value None.

    :param string: Name of a bus stop or an address
    :return: a dictionary, {_id: integer
                            address: True|False
                            bus_stop: True|False
                            latitude: float|None
                            longitude: float|None
                            }
    """
    url = (ROUTES_GENERATOR_HOST +
           ROUTES_GENERATOR_PORT +
           '/get_coordinates_from_string')

    data = {'string': string}
    response = requests.post(url, data=data, headers=headers)

    return response.json()


def coordinates_to_nearest_stop(longitude, latitude):
    """
    Finds the nearest bus stop according to the coordinates supplied.

    :param longitude: float
    :param latitude: float
    :return: a dictionary: {_id: integer
                            name: String, name of the bus stop
                            latitude: float
                            longitude: float
                            }
    """
    url = (ROUTES_GENERATOR_HOST +
           ROUTES_GENERATOR_PORT +
           '/get_nearest_stop_from_coordinates')

    data = {'lat': latitude, 'lon': longitude}
    response = requests.post(url, data=data, headers=headers)

    return response.json()


def get_route(coordinates_list):
    """
    Finds the route from a list of destinations. The first element is the
    starting point and the last element is the ending point. Other coordinates
    in the list are intermediate point to visit in increasing order.

    :param coordinates_list: list of coordinates [(longitude, latitude)]
    :return: dict, {_id: integer
                    points: coordinates_list
                    route: list of coordinates, [(longitude, latitude)] the
                        route.
                    start: coordinates_list[0]
                    end: coordinates_list[-1]
                    cost: [float], the cost in sec to get between two points on
                        the route. cost[0] = cost for route between points[0]
                        and points[1].
                    }
    """
    url = (ROUTES_GENERATOR_HOST +
           ROUTES_GENERATOR_PORT +
           '/get_route_from_coordinates')

    data = {'list': str(coordinates_list)}
    response = requests.post(url, data=data, headers=headers)
    responseJson = response.json()

    responseJson['route'] = ast.literal_eval(responseJson['route'])
    responseJson['end'] = ast.literal_eval(responseJson['end'])
    responseJson['points'] = ast.literal_eval(responseJson['points'])
    responseJson['start'] = ast.literal_eval(responseJson['start'])
    responseJson['cost'] = ast.literal_eval(responseJson['cost'])

    return responseJson


if __name__ == '__main__':
    print string_to_coordinates("sErnanders vÄG")
    print string_to_coordinates("SernandeRs VäG 10")
    print get_route([(17.6130204, 59.8545318),
                     (17.5817552, 59.8507556),
                     (17.6476356, 59.8402173)])
    print get_route([])
    print coordinates_to_nearest_stop(latitude=59.8710848,
                                      longitude=17.6546528)