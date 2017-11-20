from flask import Flask, request
from flask_sqlalchemy import SQLAlchemy
from flask_restless import APIManager
from server.models import db
from server.api import *

# Init app instance
app = Flask(__name__)

def initApiServer(conf):
    # load config
    app.config.from_object(conf)

    # init flask sqlalchemy
    db.app = app
    db.init_app(app)
    db.create_all()

    # init API endpoints
    manager = APIManager(app, flask_sqlalchemy_db=db)
    createApi(manager)

    return app
