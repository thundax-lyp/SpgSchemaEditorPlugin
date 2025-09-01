// @ts-nocheck
import BaiKe from "./data/BaiKe.json"
import CsQa from "./data/CsQa.json"
import DomainKG from "./data/DomainKG.json"
import HotpotQA from "./data/HotpotQA.json"
import Medicine from "./data/Medicine.json"
import MuSiQue from "./data/MuSiQue.json"
import RiskMining from "./data/RiskMining.json"
import SupplyChain from "./data/SupplyChain.json"
import TwoWiki from "./data/TwoWiki.json"

import SampleStep1 from "./data/SampleStep1.json"
import SampleStep2 from "./data/SampleStep2.json"
import SampleStep3 from "./data/SampleStep3.json"
import SampleStep4 from "./data/SampleStep4.json"

export const responseBody = [
    BaiKe, CsQa, DomainKG, HotpotQA, Medicine, MuSiQue, RiskMining, SupplyChain, TwoWiki
]

export const responseBodyStep = [
    SampleStep1, SampleStep2, SampleStep3, SampleStep4
    // SampleStep1
]

let currentStep = 0

export default [{
    url: '/openspg/api/schema/fetch',
    method: 'post',
    response: () => {
        const result = responseBodyStep[currentStep]
        currentStep = (currentStep + 1) % responseBodyStep.length
        return result
    }
}, {
    url: '/openspg/api/schema/focus',
    method: 'post',
    response: () => {
        return true
    }
}]
