from math import radians, cos, sin, asin, sqrt
from models import Post

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
