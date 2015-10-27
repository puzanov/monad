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
import random

def initRepeatBound(container, func, bound):
    """
    Creating a instance of container with a generator function that is calling
    the function func "random" times in the range of the bound. This function is
    similar to initRepeat in Deap but instead of N times a bound is used.

    :param container: The object to add the generator function to
    :param func: The function that should be repeated
    :param bound: A bound that func can be repeated
    :return: An instance of the container with data generated by repeating func
    """
    return container(func() for _ in xrange(random.randint(bound[0],bound[1])))

