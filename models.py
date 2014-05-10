from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
import config,datetime
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] =  config.DB_URI
db = SQLAlchemy(app)
class gsm(db.Model):
  __tablename__ = 'gsm_user'
  id = db.Column(db.String(40),primary_key=True)
  idtype = db.Column(db.String(4))
  tracker = db.Column(db.Integer)
  date = db.Column(db.DateTime)
  distance = db.Column(db.Integer)
  name = db.Column(db.String(40))
  dob = db.Column(db.String(40))
  altcontact = db.Column(db.String(11))
  def __init__(self,id,tracker,date,distance,idtype,name,dob,altcontact):
    self.id = id
    self.tracker = tracker
    self.date = date
    self.distance = distance
    self.idtype = idtype
    self.name= name
    self.dob = dob
    self.altcontact = altcontact
class tracker(db.Model):
  __tablename__ = 'gsm_tracker'
  scanrange = db.Column(db.Integer)
  trackernumber = db.Column(db.Integer,primary_key=True)
  location = db.Column(db.String(100))
  date = db.Column(db.DateTime)
  
  def __init__(self,scanrange,trackernumber,location,date):
    self.scanrange = scanrange
    self.trackernumber = trackernumber
    self.location = location
    self.date = date
