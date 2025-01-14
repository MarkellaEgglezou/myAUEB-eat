import random
import string
import firebase_admin
from firebase_admin import credentials, firestore
import random

# Initialise the access to our Database
api_key = # API
cred = credentials.Certificate(api_key)
firebase_admin.initialize_app(cred)


db = firestore.client()


# Create dishes to add in our db with json formatting
dishes = [
  {
    "itemID": "mpolonez001",
    "title": "Spaghetti Bolonez",
    "day": "Mon",
    "description": "A rich and hearty Italian pasta dish featuring spaghetti tossed in a slow-cooked meat-based tomato sauce.",
    "allergens": [],
    "imageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1p67qdUKTW2CRN-wh20946_-zgpyN3W15Ag&s",
    "type": "Lunch"
  }
]




def delete_all_dishes():
    dishes_ref = db.collection("Menu")
    docs = dishes_ref.stream()
    for doc in docs:
        doc.reference.delete()
    print("Όλα τα πιάτα διαγράφηκαν επιτυχώς.")


def add_dishes():
    for dish in dishes:
 
        doc_ref = db.collection("Menu").document(dish["itemID"])
        doc_ref.set({
            "title": dish["title"],
            "description": dish["description"],
            "itemID": dish["itemID"],
            "allergens": dish["allergens"],
            "imageUrl": dish["imageUrl"],
            "day": dish["day"],
            "type": dish["type"]
        })
        print(f"Πιάτο {dish['title']} με ID {dish['itemID']} προστέθηκε.")


delete_all_dishes()
add_dishes()