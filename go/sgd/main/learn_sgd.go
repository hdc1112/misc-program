package main

func Predict(model, data) float64 {
	var Y float64
	if model.type == "logistic" {
		logitP = 0
		for i, beta := range model.betas {
			logitP += beta * model.x[i]
		}
		Y = 1 / 1 + exp(-logitP)
	} else if model.type == "linear" {
		Y = 0.0
		for i, beta := range model.betas {
			Y += beta * model.x[i]
		}
	}
}

func Update(*model, data, learningRate float64) {
	pred := Predict(model, data)
	resid := pred - data.y
	for i, beta := range model.betas {
		model.beta[i] = model.beta[i] - learningRate * resid * mode.x[i]
	}
}

func Simulate(model) data {
	for i, _ := range model.betas {
		if i == 0 {
			data.x[0] = 1
		} else {
			data.x[i] = rnorm()
		}
	}
	if mode.type == "logistic" {
		pred := Predict(model, data)
		if runif() > pred {
			data.y = 1
		} else {
			data.y = 0
		}
	} else {
		data.y = Predict(model, data)
	}
	return data
}