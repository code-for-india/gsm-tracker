from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
import config,datetime
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] =  config.DB_URI
db = SQLAlchemy(app)
class gsm(db.Model):
  __tablename__ = 'gsm_user'
  imei = db.Column(db.String(40),primary_key=True)
  tracker = db.Column(db.Integer)
  date = db.Column(db.DateTime)
  distance = db.Column(db.Integer)
  
  def __init__(self,imei,tracker,date,distance):
    self.imei = imei
    self.tracker = tracker
    self.date = date
    self.distance = distance
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
