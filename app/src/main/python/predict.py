import joblib
import pandas.io.json
import pandas.core
from os.path import dirname, join

filename = join(dirname(__file__), "flight_delay_model.joblib")
model = joblib.load(filename)


def predict_output(input):
    ser = pandas.read_json(input, typ='series')

    dict = {'IATA_Code_Operating_Airline': ser['IATA_Code_Operating_Airline'],
            'Origin': ser['Origin'],
            'Dest': ser['Dest'],
            'DepTime': ser['DepTime'],
            'ArrTime': ser['ArrTime'],
            'DayOfWeek': ser['DayOfWeek'],
            'DayofMonth': ser['DayofMonth'],
            'Year': ser['Year'],
            'Month': ser['Month']}
    # input_data = pandas.read_json(input_json, orient="records")
    input_data = pandas.DataFrame([dict])
    # input_data = ser.to_frame('count')
    input_data["Origin"] = pandas.factorize(input_data["Origin"])[0]
    input_data["Dest"] = pandas.factorize(input_data["Dest"])[0]
    input_data["IATA_Code_Operating_Airline"] = \
    pandas.factorize(input_data["IATA_Code_Operating_Airline"])[0]

    output_data = model.predict(input_data)
    input_data.predictedDelay = output_data[0]

    return input_data.predictedDelay
