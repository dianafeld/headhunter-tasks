CLIENT_ID = "XOM30XJAJRRGJQVL1GOI4UGIAQ3RPBBGL21I3KEXIY13GSBD"
CLIENT_SECRET = "3WNHHAAWV1VSGQVRGPTTGFEUWVPIU2GFXCG01UY0H0QIW2XX"
REDIRECT_URI = "http://localhost:12345/4sq_callback"
API_DATE = "20141210"

# https://github.com/reddit/reddit/wiki/OAuth2-Python-Example

from flask import Flask
from flask import request
import requests
import urllib

app = Flask(__name__)


@app.route('/')
def homepage():
    text = '<a href="%s">Authenticate with 4sq</a>'
    return text % make_authorization_url()


def make_authorization_url():
    params = {"client_id": CLIENT_ID,
              "response_type": "code",
              "redirect_uri": REDIRECT_URI}

    url = "https://foursquare.com/oauth2/authenticate?" + urllib.parse.urlencode(params)
    return url


@app.route('/4sq_callback')
def foursq_callback():
    error = request.args.get('error', '')
    if error:
        return "Error: " + error
    code = request.args.get('code')
    access_token = get_token(code)
    return "Your e-mail is %s" % get_email(access_token)


def get_token(code):
    url = "https://foursquare.com/oauth2/access_token"
    params = {"client_id": CLIENT_ID,
              "client_secret": CLIENT_SECRET,
              "grant_type": "authorization_code",
              "redirect_uri": REDIRECT_URI,
              "code": code}

    response = requests.get(url, params=params)
    token_json = response.json()
    return token_json["access_token"]

def get_email(access_token):
    url = "https://api.foursquare.com/v2/users/self"
    params = {"oauth_token": access_token,
              "v": API_DATE,
              "m": "foursquare"}
    response = requests.get(url, params=params)
    me_json = response.json()
    return me_json["response"]["user"]["contact"]["email"]

# def update_userpic(access_token):
#     url = "https://api.foursquare.com/v2/users/self/update"
#     params = {"oauth_token": access_token,
#               "v": API_DATE,
#               "m": "swarm"}
#
#     files = {'photo': ('file.jpg', open('/home/itsnotme/enot2.jpg', 'rb'), 'image/jpeg')}
#     response = requests.post(url, params=params, files=files)
#     me_json = response.json()
#     return me_json


if __name__ == '__main__':
    app.run(debug=True, port=12345)
