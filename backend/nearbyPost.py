from flask import Flask, abort, request, jsonify
from math import radians, cos, sin, asin, sqrt
from models import Post
from app import app
import json

def haversine(lat1,lng1, lat2, lng2):
    """
    Calculate the great circle distance between two points 
    on the earth (specified in decimal degrees)
    """
    # convert decimal degrees to radians 
    lon1, lat1, lon2, lat2 = map(radians, [lat1, lng1, lat2, lng2])
    # haversine formula 
    dlon = lon2 - lon1 
    dlat = lat2 - lat1 
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a)) 
    # Radius of earth in kilometers is 6371
    km = 6371* c
    return km

def findNearbyPosts(inputJson):
    targetLat = inputJson['lat']
    targetLng = inputJson['lng']
    targetRadius = inputJson['radius']
    resultPosts = []
    posts = Post.query.all()
    for post in posts:
        lat = post.lat
        lng = post.lng
        distance = haversine(targetLat,targetLng,lat,lng)
        if distance < targetRadius:
            resultPosts.append(post)
    return resultPosts

def createGeoJson(posts):
    featureArray = []
    for post in posts:
        post_feature = {
            "type": "post",
            "geometry": {
                "type": "Point",
                "coordinates": ''
            },
            "properties": {
                "author": ''
            }
        }
        post_feature["geometry"]["coordinates"] = [post.lat, post.lng]
        post_feature["properties"]["author"] = "John Smith"
        featureArray.append(post_feature)
    return {
        "type": "FeatureCollection",
        "features": featureArray
    }

# Expects a JSON with current geolocation and radius in km
# {
#   lat: 51.503364
#   lng:  -0.127625
#   radius: 5
# }
@app.route('/api/nearby_posts', methods=['POST'])
def find_nearby_posts():
    if not request.json:
        abort(400)
    if all (key in request.json for key in ("lat","lng","radius")):
        result = findNearbyPosts(request.json)
    else:
        abort(400)
    return jsonify([r.serialize for r in result])

@app.route('/api/nearby_post_locations', methods=['POST'])
def find_neearby_post_locations():
    if not request.json:
        abort(400)
    if all (key in request.json for key in ("lat","lng","radius")):
        posts = findNearbyPosts(request.json)
        geoJson = createGeoJson(posts)
    else:
        abort(400)
    return jsonify(geoJson)