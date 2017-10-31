from flask import Flask, abort, request, jsonify
from flask_sqlalchemy import SQLAlchemy
import json, flask_restless

app = Flask(__name__)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///./db/sqlite.db'
db = SQLAlchemy(app)

from models import *
from nearbyPost import *

@app.route('/')
def hello_world():
    return 'Hello, World!'

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
    return jsonify([p.serialize for p in result])


@app.route('/post/<id>')
def show_post(id):
    post = Post.query.filter_by(id=id).first()
    respJson = json.dumps({'title': post.title, 'body': post.body})
    return respJson

manager = flask_restless.APIManager(app, flask_sqlalchemy_db=db)
manager.create_api(Post, methods=['GET','POST', 'PUT'])

