from flask import Flask, abort, request, jsonify
from flask_sqlalchemy import SQLAlchemy
import json, flask_restless

app = Flask(__name__)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///./db/sqlite.db'
db = SQLAlchemy(app)

from models import User, Post
from authenticate import *
from nearbyPost import *

@app.route('/')
def hello_world():
    return 'Hello, World!'

@app.route('/post/<id>')
def show_post(id):
    post = Post.query.filter_by(id=id).first()
    respJson = json.dumps({'title': post.title, 'body': post.body})
    return respJson

def init_db():
    db.init_app(app)
    db.app = app
    db.create_all()

manager = flask_restless.APIManager(app, flask_sqlalchemy_db=db)
post_cols = ['id','user','user.username', 'user.id','body','lat','lng']
manager.create_api(Post, methods=['GET','POST', 'PUT'], include_columns=post_cols)


if __name__ == '__main__':
    init_db()
    app.run(host='localhost', port=80)
    # app.run(host='0.0.0.0', port=80)