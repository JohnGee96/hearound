from sqlalchemy.sql import func
from app import db

def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

class Post(db.Model):
    __tablename__ = "posts"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    # geolocation = db.relationship(Geolocation, backref=db.backref('geolocations'))
    title = db.Column(db.String(100), nullable=False)
    body = db.Column(db.String(500), nullable=False)
    lat = db.Column(db.Float(9), nullable=False)
    lng = db.Column(db.Float(9), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), server_default=func.now())
    updated_at = db.Column(db.DateTime(timezone=True), server_default=func.now(),
                           onupdate=func.now())

    @property
    def serialize(self):
        return {
            'id'         : self.id,
            'title'      : self.title,
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