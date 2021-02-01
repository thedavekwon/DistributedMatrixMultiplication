import matplotlib.pyplot as plt
import pandas as pd

df = pd.read_csv("output.csv")
df = df.set_index(["N"])
df.plot()
plt.xlabel("NxN Matrix")
plt.ylabel("ms")
plt.savefig("output.png")