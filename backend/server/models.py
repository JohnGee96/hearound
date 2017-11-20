# Defines all models for database

from sqlalchemy.sql import func
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

# create db object
db = SQLAlchemy()

def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

class Post(db.Model):
    __tablename__ = "posts"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    # geolocation = db.relationship(Geolocation, backref=db.backref('geolocations'))
    user_id = db.Column(db.Integer,db.ForeignKey('users.id'))
    user = db.relationship('User', backref='post')
    body = db.Column(db.String(500), nullable=False)
    lat = db.Column(db.Float(9), nullable=False)
    lng = db.Column(db.Float(9), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), server_default=func.now())
    updated_at = db.Column(db.DateTime(timezone=True), server_default=func.now(),
                           onupdate=func.now())
    
    def getAuthor(self):
        if self.user is not None:
            return self.user.username
        else:
            return None

    @property
    def serialize(self):
        return {
            'id'         : self.id,
            'author'     : self.getAuthor(),
            'body'       : self.body,
            'lat'        : self.lat,
            'lng'        : self.lng,
            'modified_at': dump_datetime(self.created_at),
            'updated_at' : dump_datetime(self.updated_at)
        }
    
class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)  
    username = db.Column(db.String(20), nullable=False)  
    email = db.Column(db.String(50), nullable=False)
    password = db.Column(db.String(30), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), server_default=func.now())
    updated_at = db.Column(db.DateTime(timezone=True), server_default=func.now(),
                           onupdate=func.now())
   
    def __init__(self, username, email, password):
        self.username = username
        self.email = email
        self.setPassword(password)
    def setPassword(self,password):
        self.password = generate_password_hash(password)
    def checkPassword(self, password):
        return check_password_hash(self.password, password)
    def getUsername(self):
        return self.username
    def getEmail(self):
        return self.email