from flask import Flask, abort, request, jsonify
from flask_sqlalchemy import SQLAlchemy

from models import *

@app.route('/signup', methods=['POST'])
def signup():
    if not request.json:
        abort(400)
    if all (key in request.json for key in ("email","password", "username")):
        if User.query.filter_by(email=form.email.data).first():
            return "Email address already exists"
        else:
            newuser = User(request.json["email"], request.json["password"], request.json["username"])
            db.session.add(newuser)
            db.session.commit()
            return "User created!!!"
    else:
        return "Form didn't validate"


def authenticate(json):


@app.route('/api/login', methods=['POST'])
def login():
    if not request.json:
        abort(400)
    if all (key in request.json for key in ("email","password")):
        result = authenticate(request.json)
    else:
        abort(400)
    return respJson