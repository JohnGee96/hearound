from flask import Flask, abort, request
from flask_sqlalchemy import SQLAlchemy
from models import *

from app import app

@app.route('/signup', methods=['POST'])
def signup():
    if not request.json:
        abort(400)
    if all (key in request.json for key in ("email","password", "username")):
        if User.query.filter_by(email=request.json["email"]).first():
            return "Email address already exists"
        else:
            newuser = User(request.json["email"], request.json["password"], request.json["username"])
            db.session.add(newuser)
            db.session.commit()
            return "User created!!!"
    else:
        return "Form didn't validate"

def authenticate(json):
    input_email = json["email"]
    input_pwHash = json["password"]
    user = User.query.filter_by(email=input_email).first()
    if user:
        if user.password == input_pwHash:
            return True
    return False


@app.route('/api/login', methods=['POST'])
def login():
    if not request.json:
        abort(400)
    if all (key in request.json for key in ("email","password")):
        if authenticate(request.json):
            return "User logged in"
        else:
            return "Invalid Authentication"
    else:
        abort(400)
    return respJson