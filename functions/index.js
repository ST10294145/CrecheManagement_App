const functions = require("firebase-functions");
const stripe = require("stripe")("YOUR_STRIPE_SECRET_KEY"); // use live key in production
const cors = require("cors")({ origin: true });

exports.createPaymentIntent = functions.https.onRequest((req, res) => {
  cors(req, res, async () => {
    const { amount, currency } = req.body;

    try {
      const paymentIntent = await stripe.paymentIntents.create({
        amount,
        currency,
      });
      res.status(200).send({ clientSecret: paymentIntent.client_secret });
    } catch (error) {
      res.status(400).send({ error: error.message });
    }
  });
});
